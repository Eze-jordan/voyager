package com.solutechOne.voyager.service;

import com.solutechOne.voyager.enums.BoardingConfirmed;
import com.solutechOne.voyager.enums.SeatOccupationMode;
import com.solutechOne.voyager.enums.SeatReservationStatus;
import com.solutechOne.voyager.model.Departure;
import com.solutechOne.voyager.model.Reservation;
import com.solutechOne.voyager.model.Seat;
import com.solutechOne.voyager.model.SeatReserved;
import com.solutechOne.voyager.model.Travel;
import com.solutechOne.voyager.repositories.DepartureRepository;
import com.solutechOne.voyager.repositories.ReservationRepository;
import com.solutechOne.voyager.repositories.SeatRepository;
import com.solutechOne.voyager.repositories.SeatReservedRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatReservedService {

    private final SeatReservedRepository seatReservedRepository;
    private final DepartureRepository departureRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    public SeatReservedService(
            SeatReservedRepository seatReservedRepository,
            DepartureRepository departureRepository,
            SeatRepository seatRepository,
            ReservationRepository reservationRepository
    ) {
        this.seatReservedRepository = seatReservedRepository;
        this.departureRepository = departureRepository;
        this.seatRepository = seatRepository;
        this.reservationRepository = reservationRepository;
    }

    // =========================
    // INITIALIZE SEATS FOR DEPARTURE
    // =========================

    @Transactional
    public void initializeSeatsForDeparture(String departureId, List<Seat> seats) {
        if (departureId == null || departureId.isBlank()) {
            throw new IllegalArgumentException("departureId is required");
        }

        if (seats == null || seats.isEmpty()) {
            return;
        }

        Departure departure = departureRepository.findById(departureId)
                .orElseThrow(() -> new RuntimeException("Departure not found: " + departureId));

        for (Seat seat : seats) {
            if (seat == null || seat.getSeatId() == null || seat.getSeatId().isBlank()) {
                continue;
            }

            boolean exists = seatReservedRepository
                    .findByDeparture_DepartureIdAndSeat_SeatId(departureId, seat.getSeatId())
                    .isPresent();

            if (exists) {
                continue; // Si le siège est déjà réservé, on continue avec le suivant.
            }

            SeatReserved sr = new SeatReserved();
            sr.setDeparture(departure);
            sr.setSeat(seat);
            sr.setReservation(null);
            sr.setReservedStatus(SeatReservationStatus.LIBRE); // Libre au départ
            sr.setBoardingConfirmed(BoardingConfirmed.NO); // Pas encore confirmé

            seatReservedRepository.save(sr);
        }
    }

    // =========================
    // GET SEATS BY DEPARTURE
    // =========================

    public List<SeatReserved> getByDeparture(String departureId) {
        if (departureId == null || departureId.isBlank()) {
            throw new IllegalArgumentException("departureId is required");
        }

        return seatReservedRepository.findByDeparture_DepartureId(departureId);
    }

    // =========================
    // ASSIGN SEAT TO RESERVATION
    // =========================

    @Transactional
    public SeatReserved assignSeat(String reservationId, String seatId) {
        if (reservationId == null || reservationId.isBlank()) {
            throw new IllegalArgumentException("reservationId is required");
        }

        if (seatId == null || seatId.isBlank()) {
            throw new IllegalArgumentException("seatId is required");
        }

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found: " + reservationId));

        Travel travel = reservation.getTravel();
        if (travel == null || travel.getDeparture() == null) {
            throw new IllegalStateException("Reservation is not linked to a valid departure");
        }

        String departureId = travel.getDeparture().getDepartureId();

        // On récupère le mode d'occupation des sièges pour ce départ
        SeatOccupationMode mode = travel.getDeparture().getSeatOccupationMode();

        SeatReserved seatReserved = null;

        if (mode == SeatOccupationMode.RANDOM) {
            seatReserved = assignSeatRandomly(departureId, reservationId);
        } else if (mode == SeatOccupationMode.NUMBERED) {
            seatReserved = assignSeatNumbered(departureId, reservationId);
        }

        return seatReserved;
    }

    private SeatReserved assignSeatRandomly(String departureId, String reservationId) {
        SeatReserved seatReserved = seatReservedRepository
                .findFirstByDeparture_DepartureIdAndReservedStatusOrderBySeat_SeatOrderNumberAsc(
                        departureId, SeatReservationStatus.LIBRE)
                .orElseThrow(() -> new RuntimeException("No available seats for random assignment"));

        seatReserved.setReservedStatus(SeatReservationStatus.RESERVE);
        seatReserved.setReservation(reservationRepository.findById(reservationId).orElseThrow());
        seatReserved.setBoardingConfirmed(BoardingConfirmed.NO);

        return seatReservedRepository.save(seatReserved);
    }

    private SeatReserved assignSeatNumbered(String departureId, String reservationId) {
        // Ici, on attribue les sièges numérotés dans l'ordre disponible
        SeatReserved seatReserved = seatReservedRepository
                .findFirstByDeparture_DepartureIdAndReservedStatusOrderBySeat_SeatOrderNumberAsc(
                        departureId, SeatReservationStatus.LIBRE)
                .orElseThrow(() -> new RuntimeException("No available seats for numbered assignment"));

        seatReserved.setReservedStatus(SeatReservationStatus.RESERVE);
        seatReserved.setReservation(reservationRepository.findById(reservationId).orElseThrow());
        seatReserved.setBoardingConfirmed(BoardingConfirmed.NO);

        return seatReservedRepository.save(seatReserved);
    }

    // =========================
    // CONFIRM BOARDING
    // =========================

    @Transactional
    public SeatReserved confirmBoarding(String reservationId) {
        if (reservationId == null || reservationId.isBlank()) {
            throw new IllegalArgumentException("reservationId is required");
        }

        SeatReserved seatReserved = seatReservedRepository.findByReservation_ReservationId(reservationId)
                .orElseThrow(() -> new RuntimeException("No seat assigned to this reservation"));

        if (seatReserved.getReservedStatus() != SeatReservationStatus.RESERVE) {
            throw new IllegalStateException("Cannot confirm boarding for a non-reserved seat");
        }

        seatReserved.setBoardingConfirmed(BoardingConfirmed.YES);
        return seatReservedRepository.save(seatReserved);
    }

    // =========================
    // CANCEL SEAT ASSIGNMENT
    // =========================
    @Transactional
    public SeatReserved cancelSeatAssignment(String reservationId) {
        if (reservationId == null || reservationId.isBlank()) {
            throw new IllegalArgumentException("reservationId is required");
        }

        SeatReserved seatReserved = seatReservedRepository.findByReservation_ReservationId(reservationId)
                .orElseThrow(() -> new RuntimeException("No seat assigned to this reservation"));

        seatReserved.setReservation(null);
        seatReserved.setBoardingConfirmed(BoardingConfirmed.NO);
        seatReserved.setReservedStatus(SeatReservationStatus.LIBRE);

        return seatReservedRepository.save(seatReserved);
    }

    // =========================
    // AUTO-ASSIGN SEAT
    // =========================

    @Transactional
    public SeatReserved autoAssignSeat(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("reservation is required");
        }

        Travel travel = reservation.getTravel();

        if (travel == null || travel.getDeparture() == null) {
            throw new IllegalStateException("Reservation is not linked to a departure");
        }

        Departure departure = travel.getDeparture();

        // Si le mode est RANDOM, on attribue un siège aléatoire
        if (departure.getSeatOccupationMode() == SeatOccupationMode.RANDOM) {
            return assignSeatRandomly(departure.getDepartureId(), reservation.getReservationId());
        }

        // Si le mode est NUMBERED, on attribue le premier siège disponible numériquement
        return assignSeatNumbered(departure.getDepartureId(), reservation.getReservationId());
    }
}