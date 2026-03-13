package com.solutechOne.voyager.dto;

import com.solutechOne.voyager.enums.Sexe;

import java.time.LocalDate;

public class ReservationRequest {

    private String travelId;
    private String passengerName;
    private String passengerFirstname;
    private LocalDate passengerDateOfBirth;
    private Sexe passengerSex;
    private String passengerNationality;
    private String passengerMail;
    private String passengerPhone;
    private String passengerWhatsapp;
    private String ticketId;
    private String reservationReference;

    public String getTravelId() {
        return travelId;
    }

    public void setTravelId(String travelId) {
        this.travelId = travelId;
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

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    // Getters and setters

    public String getReservationReference() {
        return reservationReference;
    }

    public void setReservationReference(String reservationReference) {
        this.reservationReference = reservationReference;
    }
}