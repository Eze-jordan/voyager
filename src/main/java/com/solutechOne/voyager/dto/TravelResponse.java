package com.solutechOne.voyager.dto;

import com.solutechOne.voyager.model.Travel;

public class TravelResponse {

    public String travelId;
    public String basketId;
    public String departureId;
    public String arrivalId;

    public static TravelResponse fromEntity(Travel travel) {
        TravelResponse r = new TravelResponse();
        r.travelId = travel.getTravelId();
        r.basketId = travel.getBasket() != null ? travel.getBasket().getBasketId() : null;
        r.departureId = travel.getDeparture() != null ? travel.getDeparture().getDepartureId() : null;
        r.arrivalId = travel.getArrival() != null ? travel.getArrival().getArrivalId() : null;
        return r;
    }
}