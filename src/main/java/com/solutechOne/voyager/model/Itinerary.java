package com.solutechOne.voyager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "itineraries",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_itinerary_departure_arrival",
                columnNames = {"departure_city_id", "arrival_city_id"}
        ),
        indexes = {
                @Index(name = "idx_itinerary_departure", columnList = "departure_city_id"),
                @Index(name = "idx_itinerary_arrival", columnList = "arrival_city_id")
        }
)
public class Itinerary {

    @Id
    @Column(name = "itinerary_id", nullable = false, length = 100)
    private String id;

    // Ville de départ (obligatoire)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departure_city_id", nullable = false)
    private City departureCity;

    // Ville d’arrivée (obligatoire)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "arrival_city_id", nullable = false)
    private City arrivalCity;

    // Villes traversées (facultatif)
    @OneToMany(mappedBy = "itinerary",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OrderBy("stepOrder ASC")
    private List<ItineraryStep> steps = new ArrayList<>();

    /* =========================
       Getters & Setters
       ========================= */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public City getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(City departureCity) {
        this.departureCity = departureCity;
    }

    public City getArrivalCity() {
        return arrivalCity;
    }

    public void setArrivalCity(City arrivalCity) {
        this.arrivalCity = arrivalCity;
    }

    public List<ItineraryStep> getSteps() {
        return steps;
    }

    public void setSteps(List<ItineraryStep> steps) {
        this.steps = steps;
    }
}