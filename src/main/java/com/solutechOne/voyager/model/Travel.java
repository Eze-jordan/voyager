package com.solutechOne.voyager.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "travels")
public class Travel {

    @Id
    @Column(name = "travel_id", nullable = false, length = 60, updatable = false)
    private String travelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basket_id", nullable = false)
    private Basket basket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_id", nullable = false)
    private Departure departure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arrival_id", nullable = false)
    private TravelArrival arrival;

    @OneToMany(mappedBy = "travel")
    private List<Reservation> reservations;

    @PrePersist
    public void generateId() {
        if (this.travelId == null || this.travelId.isBlank()) {
            this.travelId = "travel-" + UUID.randomUUID();
        }
    }

    public String getTravelId() {
        return travelId;
    }

    public void setTravelId(String travelId) {
        this.travelId = travelId;
    }

    public Basket getBasket() {
        return basket;
    }

    public void setBasket(Basket basket) {
        this.basket = basket;
    }

    public Departure getDeparture() {
        return departure;
    }

    public void setDeparture(Departure departure) {
        this.departure = departure;
    }

    public TravelArrival getArrival() {
        return arrival;
    }

    public void setArrival(TravelArrival arrival) {
        this.arrival = arrival;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
}