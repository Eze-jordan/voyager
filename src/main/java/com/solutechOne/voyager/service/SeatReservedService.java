package com.solutechOne.voyager.service;

import com.solutechOne.voyager.enums.BoardingConfirmed;
import com.solutechOne.voyager.enums.SeatOccupationMode;
import com.solutechOne.voyager.enums.SeatReservationStatus;
import com.solutechOne.voyager.model.Departure;
import com.solutechOne.voyager.model.Reservation;
import com.solutechOne.voyager.model.Seat;
import com.solutechOne.voyager.model.SeatReserved;
import com.solutechOne.voyager.model.TicketPrice;
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
                continue;
            }

            SeatReserved sr = new SeatReserved();
            sr.setDeparture(departure);
            sr.setSeat(seat);
            sr.setReservation(null);
            sr.setReservedStatus(SeatReservationStatus.LIBRE);
            sr.setBoardingConfirmed(BoardingConfirmed.NO);

            seatReservedRepository.save(sr);
        }
    }

    public List<SeatReserved> getByDeparture(String departureId) {
        if (departureId == null || departureId.isBlank()) {
            throw new IllegalArgumentException("departureId is required");
        }

        return seatReservedRepository.findByDeparture_DepartureId(departureId);
    }

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

        Departure departure = getDepartureFromReservation(reservation);
        String classId = getClassIdFromReservation(reservation);

        SeatReserved seatReserved = seatReservedRepository
                .findByDeparture_DepartureIdAndSeat_SeatIdAndSeat_TravelClass_ClassId(
                        departure.getDepartureId(),
                        seatId,
                        classId
                )
                .orElseThrow(() -> new RuntimeException(
                        "Seat not found, not available for this departure, or does not match the ticket class"
                ));

        if (seatReserved.getReservedStatus() != SeatReservationStatus.LIBRE) {
            throw new IllegalStateException("This seat is already reserved");
        }

        seatReserved.setReservedStatus(SeatReservationStatus.RESERVE);
        seatReserved.setReservation(reservation);
        seatReserved.setBoardingConfirmed(BoardingConfirmed.NO);

        return seatReservedRepository.save(seatReserved);
    }

    @Transactional
    public SeatReserved autoAssignSeat(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("reservation is required");
        }

        Departure departure = getDepartureFromReservation(reservation);
        String classId = getClassIdFromReservation(reservation);

        if (departure.getSeatOccupationMode() == SeatOccupationMode.RANDOM) {
            return assignSeatByClass(departure.getDepartureId(), reservation, classId);
        }

        return assignSeatByClass(departure.getDepartureId(), reservation, classId);
    }

    private SeatReserved assignSeatByClass(String departureId, Reservation reservation, String classId) {
        SeatReserved seatReserved = seatReservedRepository
                .findFirstByDeparture_DepartureIdAndReservedStatusAndSeat_TravelClass_ClassIdOrderBySeat_SeatOrderNumberAsc(
                        departureId,
                        SeatReservationStatus.LIBRE,
                        classId
                )
                .orElseThrow(() -> new RuntimeException(
                        "No available seat for the selected ticket class"
                ));

        seatReserved.setReservedStatus(SeatReservationStatus.RESERVE);
        seatReserved.setReservation(reservation);
        seatReserved.setBoardingConfirmed(BoardingConfirmed.NO);

        return seatReservedRepository.save(seatReserved);
    }

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

    private Departure getDepartureFromReservation(Reservation reservation) {
        Travel travel = reservation.getTravel();

        if (travel == null || travel.getDeparture() == null) {
            throw new IllegalStateException("Reservation is not linked to a valid departure");
        }

        return travel.getDeparture();
    }

    private String getClassIdFromReservation(Reservation reservation) {
        TicketPrice ticketPrice = reservation.getTicketPrice();

        if (ticketPrice == null || ticketPrice.getTravelClass() == null) {
            throw new IllegalStateException("Reservation ticket price has no class");
        }

        return ticketPrice.getTravelClass().getClassId();
    }
}