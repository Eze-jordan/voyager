package com.solutechOne.voyager.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "itinerary_steps")
public class ItineraryStep {

    @Id
    @Column(name = "step_id", nullable = false, updatable = false)
    private String stepId;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itinerary_id", nullable = false)
    private Itinerary itinerary;


    @Column(name = "step_city_name", length = 150)
    private String stepCityName;

    @NotNull
    @Min(1)
    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @PrePersist
    public void generateId() {
        if (this.stepId == null || this.stepId.isBlank()) {
            this.stepId = "step-" + UUID.randomUUID();
        }
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public Itinerary getItinerary() {
        return itinerary;
    }

    public void setItinerary(Itinerary itinerary) {
        this.itinerary = itinerary;
    }



    public String getStepCityName() {
        return stepCityName;
    }

    public void setStepCityName(String stepCityName) {
        this.stepCityName = stepCityName;
    }

    public Integer getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(Integer stepOrder) {
        this.stepOrder = stepOrder;
    }
}