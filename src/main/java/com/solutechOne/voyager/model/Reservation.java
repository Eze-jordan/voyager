package com.solutechOne.voyager.model;

import com.solutechOne.voyager.enums.ReservationStatus;
import com.solutechOne.voyager.enums.Sexe;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @Column(name = "reservation_id", nullable = false, length = 100)
    private String reservationId;  // Utilisation de String pour l'UUID

    @Column(name = "reservation_reference", nullable = false, unique = true, length = 50)
    private String reservationReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_id", nullable = false)
    private Travel travel;  // Référence au voyage

    @Column(name = "passenger_name", nullable = false, length = 50)
    private String passengerName;

    @Column(name = "passenger_firstname", nullable = false, length = 50)
    private String passengerFirstname;

    @Column(name = "passenger_dateofbirth", nullable = false)
    private LocalDate passengerDateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "passenger_sexe", nullable = false, length = 10)
    private Sexe passengerSex;

    @Column(name = "passenger_nationality", nullable = false, length = 50)
    private String passengerNationality;

    @Column(name = "passenger_mail", length = 50)
    private String passengerMail;

    @Column(name = "passenger_phone", length = 18)
    private String passengerPhone;

    @Column(name = "passenger_whatsapp", length = 18)
    private String passengerWhatsapp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private TicketPrice ticketPrice;  // Prix du billet associé à la réservation

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_confirmed", nullable = false, length = 10)
    private ReservationStatus reservationConfirmed;

    @PrePersist
    public void generateReservationReference() {
        if (this.reservationReference == null) {
            this.reservationReference = "RES-" + UUID.randomUUID().toString();  // Génération d'une référence de réservation unique
        }

        // Génération d'un UUID personnalisé pour l'ID de la réservation
        if (this.reservationId == null) {
            this.reservationId = "reser-" + UUID.randomUUID().toString();  // Préfixe personnalisé
        }
    }

    // Getters et Setters
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

    public Travel getTravel() {
        return travel;
    }

    public void setTravel(Travel travel) {
        this.travel = travel;
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

    public TicketPrice getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(TicketPrice ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public ReservationStatus getReservationConfirmed() {
        return reservationConfirmed;
    }

    public void setReservationConfirmed(ReservationStatus reservationConfirmed) {
        this.reservationConfirmed = reservationConfirmed;
    }
}