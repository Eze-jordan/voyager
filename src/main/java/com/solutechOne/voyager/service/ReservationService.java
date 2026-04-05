package com.solutechOne.voyager.service;

import com.solutechOne.voyager.enums.BasketStatus;
import com.solutechOne.voyager.enums.ReservationStatus;
import com.solutechOne.voyager.enums.Sexe;
import com.solutechOne.voyager.model.Basket;
import com.solutechOne.voyager.model.Reservation;
import com.solutechOne.voyager.model.TicketPrice;
import com.solutechOne.voyager.model.Travel;
import com.solutechOne.voyager.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TicketPriceRepository ticketPriceRepository;
    private final TravelRepository travelRepository;
    private final BasketRepository basketRepository;
    private final SeatReservedService seatReservedService;
    private final TicketPriceService ticketPriceService;
    public ReservationService(
            ReservationRepository reservationRepository,
            TicketPriceRepository ticketPriceRepository,
            TravelRepository travelRepository,
            BasketRepository basketRepository, SeatReservedService seatReservedService, TicketPriceService ticketPriceService
    ) {
        this.reservationRepository = reservationRepository;
        this.ticketPriceRepository = ticketPriceRepository;
        this.travelRepository = travelRepository;
        this.basketRepository = basketRepository;
        this.seatReservedService = seatReservedService;
        this.ticketPriceService = ticketPriceService;
    }

    // =========================
    // CREATE RESERVATION
    // =========================
    @Transactional
    public Reservation createReservation(
            String travelId,
            String passengerName,
            String passengerFirstname,
            LocalDate passengerDateOfBirth,
            Sexe passengerSex,
            String passengerNationality,
            String passengerMail,
            String passengerPhone,
            String passengerWhatsapp,
            String ticketId
    ) {
        // Valide les entrées de la réservation
        validateReservationInput(
                travelId,
                passengerName,
                passengerFirstname,
                passengerDateOfBirth,
                passengerSex,
                passengerNationality,
                ticketId
        );

        // Récupère l'objet Travel (le voyage)
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new RuntimeException("Travel not found for ID: " + travelId));

        // Vérifie si le voyage a une liaison avec un départ
        if (travel.getDeparture() == null) {
            throw new IllegalStateException("Travel is not linked to a departure.");
        }

        // Récupère le panier associé au voyage
        Basket basket = travel.getBasket();
        if (basket == null) {
            throw new IllegalStateException("Travel is not linked to a basket.");
        }

        // Vérifie si le panier est déjà payé (on ne peut pas ajouter de réservation à un panier payé)
        if (basket.getBasketStatus() == BasketStatus.PAYE) {
            throw new IllegalStateException("Cannot add reservation to a paid basket.");
        }

        // Récupère le prix du ticket
        TicketPrice ticketPrice = ticketPriceRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found for ID: " + ticketId));

        // Crée une nouvelle réservation
        Reservation reservation = new Reservation();
        reservation.setTravel(travel);
        reservation.setTicketPrice(ticketPrice);
        reservation.setPassengerName(passengerName.trim());
        reservation.setPassengerFirstname(passengerFirstname.trim());
        reservation.setPassengerDateOfBirth(passengerDateOfBirth);
        reservation.setPassengerSex(passengerSex);
        reservation.setPassengerNationality(passengerNationality.trim());
        reservation.setPassengerMail(normalize(passengerMail));
        reservation.setPassengerPhone(normalize(passengerPhone));
        reservation.setPassengerWhatsapp(normalize(passengerWhatsapp));
        reservation.setReservationConfirmed(ReservationStatus.NO);

        // Associe la réservation au panier
        reservation.setBasket(basket);

        // Sauvegarde la réservation
        Reservation savedReservation = reservationRepository.save(reservation);

        // Effectue l'assignation automatique du siège
        seatReservedService.autoAssignSeat(savedReservation);

        // Recalcule le montant total du panier en fonction des réservations
        recalcBasketFromReservations(basket);

        return savedReservation;
    }

    // =========================
    // GET BY REFERENCE
    // =========================

    public Reservation getReservationByReference(String reservationReference) {
        if (reservationReference == null || reservationReference.isBlank()) {
            throw new IllegalArgumentException("Reservation reference is required.");
        }

        return reservationRepository.findByReservationReference(reservationReference)
                .orElseThrow(() -> new RuntimeException(
                        "Reservation not found with reference: " + reservationReference
                ));
    }

    // =========================
    // RECALCULATE BASKET
    // =========================

    private void recalcBasketFromReservations(Basket basket) {
        List<Reservation> reservations = reservationRepository
                .findByTravel_Basket_BasketId(basket.getBasketId());

        BigDecimal amount = reservations.stream()
                .map(r -> {
                    if (r.getTicketPrice() == null) {
                        return BigDecimal.ZERO;
                    }
                    return ticketPriceService.calculateFinalPrice(r.getTicketPrice().getPriceId());
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        basket.setBasketAmount(amount);
        basket.setNumberOfReservations(reservations.size());

        BigDecimal rate = basket.getCompany() != null && basket.getCompany().getRateFees() != null
                ? basket.getCompany().getRateFees()
                : BigDecimal.ZERO;

        BigDecimal fees = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = amount.add(fees).setScale(2, RoundingMode.HALF_UP);

        basket.setBasketFees(fees);
        basket.setBasketTotalAmount(total);

        basketRepository.save(basket);
    }

    // =========================
    // VALIDATION
    // =========================

    private void validateReservationInput(
            String travelId,
            String passengerName,
            String passengerFirstname,
            LocalDate passengerDateOfBirth,
            Sexe passengerSex,
            String passengerNationality,
            String ticketId
    ) {
        if (travelId == null || travelId.isBlank()) {
            throw new IllegalArgumentException("Travel ID is required.");
        }

        if (ticketId == null || ticketId.isBlank()) {
            throw new IllegalArgumentException("Ticket ID is required.");
        }

        if (passengerName == null || passengerName.isBlank()) {
            throw new IllegalArgumentException("Passenger name is required.");
        }

        if (passengerFirstname == null || passengerFirstname.isBlank()) {
            throw new IllegalArgumentException("Passenger firstname is required.");
        }

        if (passengerDateOfBirth == null) {
            throw new IllegalArgumentException("Passenger date of birth is required.");
        }

        if (passengerSex == null) {
            throw new IllegalArgumentException("Passenger sex is required.");
        }

        if (passengerNationality == null || passengerNationality.isBlank()) {
            throw new IllegalArgumentException("Passenger nationality is required.");
        }
    }

    // =========================
    // HELPERS
    // =========================

    private String normalize(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}