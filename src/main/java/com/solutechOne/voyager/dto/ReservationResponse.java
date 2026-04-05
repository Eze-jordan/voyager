package com.solutechOne.voyager.dto;

import com.solutechOne.voyager.enums.ReservationStatus;
import com.solutechOne.voyager.enums.Sexe;
import com.solutechOne.voyager.model.Reservation;

import java.time.LocalDate;

public class ReservationResponse {

    private String reservationId;
    private String reservationReference;
    private ReservationStatus reservationConfirmed;

    private String travelId;
    private String ticketId;

    private String passengerName;
    private String passengerFirstname;
    private LocalDate passengerDateOfBirth;
    private Sexe passengerSex;
    private String passengerNationality;
    private String passengerMail;
    private String passengerPhone;
    private String passengerWhatsapp;

    public ReservationResponse(Reservation reservation) {
        this.reservationId = reservation.getReservationId();
        this.reservationReference = reservation.getReservationReference();
        this.reservationConfirmed = reservation.getReservationConfirmed();

        this.travelId = reservation.getTravel() != null ? reservation.getTravel().getTravelId() : null;
        this.ticketId = reservation.getTicketPrice() != null ? reservation.getTicketPrice().getPriceId() : null;

        this.passengerName = reservation.getPassengerName();
        this.passengerFirstname = reservation.getPassengerFirstname();
        this.passengerDateOfBirth = reservation.getPassengerDateOfBirth();
        this.passengerSex = reservation.getPassengerSex();
        this.passengerNationality = reservation.getPassengerNationality();
        this.passengerMail = reservation.getPassengerMail();
        this.passengerPhone = reservation.getPassengerPhone();
        this.passengerWhatsapp = reservation.getPassengerWhatsapp();
    }

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

    public String getTravelId() {
        return travelId;
    }

    public void setTravelId(String travelId) {
        this.travelId = travelId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
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

    public LocalDate getPassengerDateOfBirth() {
        return passengerDateOfBirth;
    }

    public void setPassengerDateOfBirth(LocalDate passengerDateOfBirth) {
        this.passengerDateOfBirth = passengerDateOfBirth;
    }

    public Sexe getPassengerSex() {
        return passengerSex;
    }

    public void setPassengerSex(Sexe passengerSex) {
        this.passengerSex = passengerSex;
    }

    public String getPassengerNationality() {
        return passengerNationality;
    }

    public void setPassengerNationality(String passengerNationality) {
        this.passengerNationality = passengerNationality;
    }

    public String getPassengerMail() {
        return passengerMail;
    }

    public void setPassengerMail(String passengerMail) {
        this.passengerMail = passengerMail;
    }

    public String getPassengerPhone() {
        return passengerPhone;
    }

    public void setPassengerPhone(String passengerPhone) {
        this.passengerPhone = passengerPhone;
    }

    public String getPassengerWhatsapp() {
        return passengerWhatsapp;
    }

    public void setPassengerWhatsapp(String passengerWhatsapp) {
        this.passengerWhatsapp = passengerWhatsapp;
    }
}