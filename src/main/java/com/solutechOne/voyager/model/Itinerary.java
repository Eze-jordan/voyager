package com.solutechOne.voyager.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "itineraries",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_itinerary_departure_arrival",
                columnNames = {"departure_city_id", "arrival_city_id"}
        )
)
public class Itinerary {

    @Id
    @Column(name = "itinerary_id", nullable = false, updatable = false)
    private String iitineraryId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_city_id", nullable = false)
    private City departureCity;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arrival_city_id", nullable = false)
    private City arrivalCity;

    @JsonManagedReference
    @OneToMany(mappedBy = "itinerary", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stepOrder ASC")
    private List<ItineraryStep> steps = new ArrayList<>();

    @PrePersist
    public void generateId() {
        if (this.iitineraryId == null) {
            this.iitineraryId = "itinerary-" + UUID.randomUUID();
        }
    }

    // 🔥 IMPORTANT
    public void setSteps(List<ItineraryStep> steps) {
        this.steps.clear();
        if (steps != null) {
            for (ItineraryStep step : steps) {
                step.setItinerary(this);
                this.steps.add(step);
            }
        }
    }

    // getters setters
    public String getIitineraryId() { return iitineraryId; }
    public void setIitineraryId(String iitineraryId) { this.iitineraryId = iitineraryId; }

    public City getDepartureCity() { return departureCity; }
    public void setDepartureCity(City departureCity) { this.departureCity = departureCity; }

    public City getArrivalCity() { return arrivalCity; }
    public void setArrivalCity(City arrivalCity) { this.arrivalCity = arrivalCity; }

    public List<ItineraryStep> getSteps() { return steps; }
}