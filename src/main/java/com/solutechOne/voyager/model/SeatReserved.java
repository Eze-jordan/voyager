package com.solutechOne.voyager.model;

import com.solutechOne.voyager.enums.BoardingConfirmed;
import com.solutechOne.voyager.enums.SeatReservationStatus;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "seat_reserved")
public class SeatReserved {

    @Id
    @Column(name = "reserved_id", nullable = false, length = 60, updatable = false)
    private String reservedId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_id", nullable = false)
    private Departure departure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(name = "boarding_confirmed", nullable = false, length = 10)
    private BoardingConfirmed boardingConfirmed;

    @Enumerated(EnumType.STRING)
    @Column(name = "reserved_status", nullable = false, length = 20)
    private SeatReservationStatus reservedStatus;

    @PrePersist
    public void prePersist() {
        if (this.reservedId == null || this.reservedId.isBlank()) {
            this.reservedId = "seatres-" + UUID.randomUUID();
        }
        if (this.boardingConfirmed == null) {
            this.boardingConfirmed = BoardingConfirmed.NO;
        }
        if (this.reservedStatus == null) {
            this.reservedStatus = SeatReservationStatus.LIBRE;
        }
    }

    public String getReservedId() {
        return reservedId;
    }

    public void setReservedId(String reservedId) {
        this.reservedId = reservedId;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public Departure getDeparture() {
        return departure;
    }

    public void setDeparture(Departure departure) {
        this.departure = departure;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public BoardingConfirmed getBoardingConfirmed() {
        return boardingConfirmed;
    }

    public void setBoardingConfirmed(BoardingConfirmed boardingConfirmed) {
        this.boardingConfirmed = boardingConfirmed;
    }

    public SeatReservationStatus getReservedStatus() {
        return reservedStatus;
    }

    public void setReservedStatus(SeatReservationStatus reservedStatus) {
        this.reservedStatus = reservedStatus;
    }
}