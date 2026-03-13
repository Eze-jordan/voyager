package com.solutechOne.voyager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(
        name = "itinerary_steps",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_itinerary_step_order",
                        columnNames = {"itinerary_id", "step_order"}
                ),
                @UniqueConstraint(
                        name = "uq_itinerary_step_city",
                        columnNames = {"itinerary_id", "city_id"}
                )
        },
        indexes = {
                @Index(name = "idx_itinerary_steps_itinerary", columnList = "itinerary_id"),
                @Index(name = "idx_itinerary_steps_city", columnList = "city_id")
        }
)
public class ItineraryStep {

    @Id
    @Column(name = "step_id", nullable = false, length = 100)
    private String id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "itinerary_id", nullable = false)
    private Itinerary itinerary;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @NotNull
    @Min(1)
    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    /* =========================
       Getters & Setters
       ========================= */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Itinerary getItinerary() {
        return itinerary;
    }

    public void setItinerary(Itinerary itinerary) {
        this.itinerary = itinerary;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Integer getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(Integer stepOrder) {
        this.stepOrder = stepOrder;
    }
}