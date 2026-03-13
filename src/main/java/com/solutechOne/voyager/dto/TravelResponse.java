package com.solutechOne.voyager.dto;

import com.solutechOne.voyager.model.Travel;

public class TravelResponse {

    public String travelId;
    public String basketId;
    public String departureId;
    public String arrivalId;

    public static TravelResponse fromEntity(Travel t) {
        TravelResponse r = new TravelResponse();
        r.travelId = t.getTravelId();
        r.basketId = t.getBasket() != null ? t.getBasket().getBasketId() : null;

        // ✅ ICI: adapte uniquement si tes getters ne sont pas getDepartureId/getArrivalId
        r.departureId = t.getDeparture() != null ? t.getDeparture().getDepartureId() : null;
        r.arrivalId = t.getArrival() != null ? t.getArrival().getArrivalId() : null;

        return r;
    }

    public String getTravelId() {
        return travelId;
    }

    public void setTravelId(String travelId) {
        this.travelId = travelId;
    }

    public String getBasketId() {
        return basketId;
    }

    public void setBasketId(String basketId) {
        this.basketId = basketId;
    }

    public String getDepartureId() {
        return departureId;
    }

    public void setDepartureId(String departureId) {
        this.departureId = departureId;
    }

    public String getArrivalId() {
        return arrivalId;
    }

    public void setArrivalId(String arrivalId) {
        this.arrivalId = arrivalId;
    }
}