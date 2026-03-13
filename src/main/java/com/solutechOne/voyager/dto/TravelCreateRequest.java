package com.solutechOne.voyager.dto;

public class TravelCreateRequest {
    public String basketId;
    public String departureId;
    public String arrivalId;
    public String ticketPriceId;


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

    public String getTicketPriceId() {
        return ticketPriceId;
    }

    public void setTicketPriceId(String ticketPriceId) {
        this.ticketPriceId = ticketPriceId;
    }
}