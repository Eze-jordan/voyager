package com.solutechOne.voyager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(
        name = "seats",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_means_seat_ref", columnNames = {"means_id", "seat_reference"}),
                @UniqueConstraint(name = "uk_means_seat_order", columnNames = {"means_id", "seat_order_number"})
        },
        indexes = {
                @Index(name = "idx_seats_means", columnList = "means_id"),
                @Index(name = "idx_seats_class", columnList = "class_id")
        }
)
public class Seat {

    @Id
    @Column(name = "seat_id", nullable = false, length = 60)
    private String seatId;

    // ✅ Relation vers TransportMeans
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "means_id", nullable = false)
    private TransportMeans means;

    // ✅ Relation vers TravelClass (optionnel)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private TravelClass travelClass;

    @Column(name = "seat_reference", length = 6, nullable = false)
    private String seatReference;

    @Column(name = "seat_order_number", nullable = false)
    private Integer seatOrderNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_status", nullable = false, length = 10)
    private SeatStatus seatStatus = SeatStatus.ACTIF;

    public enum SeatStatus { ACTIF, EXCLURE }

    public Seat() {}

    // getters/setters
    public String getSeatId() { return seatId; }
    public void setSeatId(String seatId) { this.seatId = seatId; }

    public TransportMeans getMeans() { return means; }
    public void setMeans(TransportMeans means) { this.means = means; }

    public TravelClass getTravelClass() { return travelClass; }
    public void setTravelClass(TravelClass travelClass) { this.travelClass = travelClass; }

    public String getSeatReference() { return seatReference; }
    public void setSeatReference(String seatReference) { this.seatReference = seatReference; }

    public Integer getSeatOrderNumber() { return seatOrderNumber; }
    public void setSeatOrderNumber(Integer seatOrderNumber) { this.seatOrderNumber = seatOrderNumber; }

    public SeatStatus getSeatStatus() { return seatStatus; }
    public void setSeatStatus(SeatStatus seatStatus) { this.seatStatus = seatStatus; }

    // ✅ Pour renvoyer l'id sans DTO
    @Transient
    public String getMeansId() {
        return means != null ? means.getMeansId() : null;
    }

    @Transient
    public String getClassId() {
        return travelClass != null ? travelClass.getClassId() : null;
    }
}