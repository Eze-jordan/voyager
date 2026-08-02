package com.solutechOne.voyager.dto.cashier;

public class AddCashierTravelRequest {

    private String departureId;
    private String arrivalId;

    public AddCashierTravelRequest() {
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