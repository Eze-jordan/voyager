package com.solutechOne.voyager.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(
        name = "invoice_lines",
        indexes = {
                @Index(
                        name = "idx_invoice_line_invoice",
                        columnList = "invoice_id"
                )
        }
)
public class InvoiceLine {

    @Id
    @Column(
            name = "invoice_line_id",
            nullable = false,
            length = 60,
            updatable = false
    )
    private String invoiceLineId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    // =========================================================
    // RÉSERVATION
    // =========================================================

    @Column(name = "reservation_id", length = 100)
    private String reservationId;

    @Column(name = "reservation_reference", length = 100)
    private String reservationReference;

    // =========================================================
    // PASSAGER
    // =========================================================

    @Column(name = "passenger_name", length = 100)
    private String passengerName;

    @Column(name = "passenger_firstname", length = 100)
    private String passengerFirstname;

    // =========================================================
    // BILLET
    // =========================================================

    @Column(name = "ticket_id", length = 60)
    private String ticketId;

    @Column(name = "ticket_title", length = 100)
    private String ticketTitle;

    @Column(name = "ticket_description", length = 255)
    private String ticketDescription;

    @Column(name = "travel_class", length = 100)
    private String travelClass;

    @Column(
            name = "unit_price",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal unitPrice;

    // =========================================================
    // TRAJET
    // =========================================================

    @Column(name = "departure_reference", length = 100)
    private String departureReference;

    @Column(name = "departure_city", length = 100)
    private String departureCity;

    @Column(name = "arrival_city", length = 100)
    private String arrivalCity;

    @Column(name = "departure_date")
    private LocalDate departureDate;

    @Column(name = "departure_time")
    private LocalTime departureTime;

    @Column(name = "arrival_date")
    private LocalDate arrivalDate;

    @Column(name = "arrival_time")
    private LocalTime arrivalTime;

    @PrePersist
    public void prePersist() {
        if (invoiceLineId == null || invoiceLineId.isBlank()) {
            invoiceLineId = "invoice-line-" + UUID.randomUUID();
        }
    }

    public String getInvoiceLineId() {
        return invoiceLineId;
    }

    public void setInvoiceLineId(String invoiceLineId) {
        this.invoiceLineId = invoiceLineId;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
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

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketTitle() {
        return ticketTitle;
    }

    public void setTicketTitle(String ticketTitle) {
        this.ticketTitle = ticketTitle;
    }

    public String getTicketDescription() {
        return ticketDescription;
    }

    public void setTicketDescription(String ticketDescription) {
        this.ticketDescription = ticketDescription;
    }

    public String getTravelClass() {
        return travelClass;
    }

    public void setTravelClass(String travelClass) {
        this.travelClass = travelClass;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getDepartureReference() {
        return departureReference;
    }

    public void setDepartureReference(String departureReference) {
        this.departureReference = departureReference;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public String getArrivalCity() {
        return arrivalCity;
    }

    public void setArrivalCity(String arrivalCity) {
        this.arrivalCity = arrivalCity;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}