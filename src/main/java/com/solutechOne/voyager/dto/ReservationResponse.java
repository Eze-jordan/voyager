package com.solutechOne.voyager.dto;

import com.solutechOne.voyager.model.Reservation;
import com.solutechOne.voyager.enums.ReservationStatus;

public class ReservationResponse {

    private String reservationId;
    private String reservationReference;
    private ReservationStatus reservationConfirmed;
    private String passengerName;
    private String passengerFirstname;
    private String travelId;

    // Constructeur à partir de l'entité Reservation
    public ReservationResponse(Reservation reservation) {
        this.reservationId = reservation.getReservationId();
        this.reservationReference = reservation.getReservationReference();
        this.reservationConfirmed = reservation.getReservationConfirmed();
        this.passengerName = reservation.getPassengerName();
        this.passengerFirstname = reservation.getPassengerFirstname();
        this.travelId = reservation.getTravel().getTravelId();  // Assurez-vous que `travel` existe et a `getTravelId()`
    }

    // Getters et setters
    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getReservationReference() {
        return reservationReference;
    }

    public void setReservationReference(String reservationReference) {
        this.reservationReference = reservationReference;
    }

    public ReservationStatus getReservationConfirmed() {
        return reservationConfirmed;
    }

    public void setReservationConfirmed(ReservationStatus reservationConfirmed) {
        this.reservationConfirmed = reservationConfirmed;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerFirstname() {
        return passengerFirstname;
    }

    public void setPassengerFirstname(String passengerFirstname) {
        this.passengerFirstname = passengerFirstname;
    }

    public String getTravelId() {
        return travelId;
    }

    public void setTravelId(String travelId) {
        this.travelId = travelId;
    }
}