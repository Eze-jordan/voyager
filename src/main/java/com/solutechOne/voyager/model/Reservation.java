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
    @Column(name = "reservation_id", nullable = false, length = 100, updatable = false)
    private String reservationId;

    @Column(name = "reservation_reference", nullable = false, unique = true, length = 50, updatable = false)
    private String reservationReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_id", nullable = false)
    private Travel travel;

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

    @Column(name = "passenger_mail", length = 100)
    private String passengerMail;

    @Column(name = "passenger_phone", length = 20)
    private String passengerPhone;

    @Column(name = "passenger_whatsapp", length = 20)
    private String passengerWhatsapp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private TicketPrice ticketPrice;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basket_id", nullable = false)  // Référence vers le panier (Basket)
    private Basket basket;  // La relation vers Basket
    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_confirmed", nullable = false, length = 10)
    private ReservationStatus reservationConfirmed;

    @PrePersist
    public void prePersist() {
        if (this.reservationReference == null || this.reservationReference.isBlank()) {
            this.reservationReference = "RES-" + UUID.randomUUID().toString();
        }

        if (this.reservationId == null || this.reservationId.isBlank()) {
            this.reservationId = "reser-" + UUID.randomUUID().toString();
        }

        if (this.reservationConfirmed == null) {
            this.reservationConfirmed = ReservationStatus.NO;
        }
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
    public Basket getBasket() {
        return basket;
    }

    public void setBasket(Basket basket) {
        this.basket = basket;
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