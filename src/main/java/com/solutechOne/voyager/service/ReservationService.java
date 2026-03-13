package com.solutechOne.voyager.service;

import com.solutechOne.voyager.enums.ReservationStatus;
import com.solutechOne.voyager.enums.Sexe;
import com.solutechOne.voyager.model.*;
import com.solutechOne.voyager.repositories.ReservationRepository;
import com.solutechOne.voyager.repositories.TicketPriceRepository;
import com.solutechOne.voyager.repositories.TravelRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TicketPriceRepository ticketPriceRepository;
    private final TravelRepository travelRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              TicketPriceRepository ticketPriceRepository,
                              TravelRepository travelRepository) {
        this.reservationRepository = reservationRepository;
        this.ticketPriceRepository = ticketPriceRepository;
        this.travelRepository = travelRepository;
    }

    // Créer une nouvelle réservation
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
        // Recherche du voyage par ID
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new RuntimeException("Travel not found for ID: " + travelId));

        // Recherche du prix du ticket par ID
        TicketPrice ticketPrice = ticketPriceRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found for ID: " + ticketId));

        // Validation des informations du passager
        if (passengerName == null || passengerFirstname == null || passengerDateOfBirth == null) {
            throw new IllegalArgumentException("Passenger details (name, firstname, or date of birth) are missing.");
        }

        // Création de la réservation
        Reservation reservation = new Reservation();
        reservation.setPassengerName(passengerName);
        reservation.setPassengerFirstname(passengerFirstname);
        reservation.setPassengerDateOfBirth(passengerDateOfBirth);
        reservation.setPassengerSex(passengerSex);
        reservation.setPassengerNationality(passengerNationality);
        reservation.setPassengerMail(passengerMail);
        reservation.setPassengerPhone(passengerPhone);
        reservation.setPassengerWhatsapp(passengerWhatsapp);

        // Référence au ticket et voyage
        reservation.setTicketPrice(ticketPrice);
        reservation.setTravel(travel);

        // Statut de la réservation : non confirmé au départ
        reservation.setReservationConfirmed(ReservationStatus.NO);

        // Enregistrement de la réservation dans la base de données
        return reservationRepository.save(reservation);
    }

    // Rechercher une réservation par référence
    public Reservation getReservationByReference(String reservationReference) {
        // Recherche de la réservation par sa référence
        return reservationRepository.findByReservationReference(reservationReference)
                .orElseThrow(() -> new RuntimeException("Reservation not found with reference: " + reservationReference));
    }
}