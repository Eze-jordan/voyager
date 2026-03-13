package com.solutechOne.voyager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solutechOne.voyager.enums.ArrivalStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(
        name = "travel_arrival",
        indexes = {
                @Index(name = "idx_arrival_departure", columnList = "departure_id"),
                @Index(name = "idx_arrival_city", columnList = "arrival_city_id"),
                @Index(name = "idx_arrival_status", columnList = "arrival_status"),
                @Index(name = "idx_arrival_date", columnList = "arrival_date")
        }
)
public class TravelArrival {

    @Id
    @Column(name = "arrival_id", nullable = false, length = 60, updatable = false)
    private String arrivalId;

    // ✅ Relation vers Departure
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departure_id", nullable = false)
    private Departure departure;

    // ✅ Relation vers City
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "arrival_city_id", nullable = false)
    private City arrivalCity;

    @Column(name = "arrival_date", nullable = false)
    private LocalDate arrivalDate;

    @Column(name = "arrival_time", nullable = false)
    private LocalTime arrivalTime;

    // ✅ Relation vers Place
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "arrival_place_id", nullable = false)
    private Place arrivalPlace;

    @Enumerated(EnumType.STRING)
    @Column(name = "arrival_status", nullable = false, length = 20)
    private ArrivalStatus arrivalStatus = ArrivalStatus.CONFIRME; // si ton enum a PROGRAMME

    // =========================
    // IDs pour le BODY (pas DB)
    // =========================
    @Transient
    private String departureId;

    @Transient
    private String arrivalCityId;

    @Transient
    private String arrivalPlaceId;

    @PrePersist
    public void generateIdAndDefaults() {
        if (this.arrivalId == null) {
            this.arrivalId = "arrival-" + UUID.randomUUID();
        }
        if (this.arrivalStatus == null) {
            // adapte si ton enum n’a pas PROGRAMME
            this.arrivalStatus = ArrivalStatus.CONFIRME;
        }
    }

    // ===== getters/setters =====

    public String getArrivalId() { return arrivalId; }
    public void setArrivalId(String arrivalId) { this.arrivalId = arrivalId; }

    public Departure getDeparture() { return departure; }
    public void setDeparture(Departure departure) { this.departure = departure; }

    public City getArrivalCity() { return arrivalCity; }
    public void setArrivalCity(City arrivalCity) { this.arrivalCity = arrivalCity; }

    public LocalDate getArrivalDate() { return arrivalDate; }
    public void setArrivalDate(LocalDate arrivalDate) { this.arrivalDate = arrivalDate; }

    public LocalTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public Place getArrivalPlace() { return arrivalPlace; }
    public void setArrivalPlace(Place arrivalPlace) { this.arrivalPlace = arrivalPlace; }

    public ArrivalStatus getArrivalStatus() { return arrivalStatus; }
    public void setArrivalStatus(ArrivalStatus arrivalStatus) { this.arrivalStatus = arrivalStatus; }

    // ✅ champs body
    public String getDepartureId() { return departureId; }
    public void setDepartureId(String departureId) { this.departureId = departureId; }

    public String getArrivalCityId() { return arrivalCityId; }
    public void setArrivalCityId(String arrivalCityId) { this.arrivalCityId = arrivalCityId; }

    public String getArrivalPlaceId() { return arrivalPlaceId; }
    public void setArrivalPlaceId(String arrivalPlaceId) { this.arrivalPlaceId = arrivalPlaceId; }

    // ✅ IDs exposés en réponse sans DTO (lecture depuis relations)
    @Transient
    public String getDepartureIdOut() { return departure != null ? departure.getDepartureId() : null; }

    @Transient
    public String getArrivalCityIdOut() { return arrivalCity != null ? arrivalCity.getCityId() : null; }

    @Transient
    public String getArrivalPlaceIdOut() { return arrivalPlace != null ? arrivalPlace.getPlaceId() : null; }
}