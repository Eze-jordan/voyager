package com.solutechOne.voyager.dto;

import java.math.BigDecimal;
import java.util.List;

public class TravelWithTicketsResponse {

    private String travelId;
    private String basketId;
    private String departureId;
    private String arrivalId;
    private List<TicketItemResponse> tickets;

    public TravelWithTicketsResponse() {
    }

    public TravelWithTicketsResponse(String travelId, String basketId, String departureId, String arrivalId, List<TicketItemResponse> tickets) {
        this.travelId = travelId;
        this.basketId = basketId;
        this.departureId = departureId;
        this.arrivalId = arrivalId;
        this.tickets = tickets;
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

    public List<TicketItemResponse> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketItemResponse> tickets) {
        this.tickets = tickets;
    }

    public static class TicketItemResponse {
        private String priceId;
        private String ticketTitle;
        private BigDecimal ticketPrice;
        private String classId;
        private String classDesignation;

        public TicketItemResponse() {
        }

        public TicketItemResponse(String priceId, String ticketTitle, BigDecimal ticketPrice, String classId, String classDesignation) {
            this.priceId = priceId;
            this.ticketTitle = ticketTitle;
            this.ticketPrice = ticketPrice;
            this.classId = classId;
            this.classDesignation = classDesignation;
        }

        public String getPriceId() {
            return priceId;
        }

        public void setPriceId(String priceId) {
            this.priceId = priceId;
        }

        public String getTicketTitle() {
            return ticketTitle;
        }

        public void setTicketTitle(String ticketTitle) {
            this.ticketTitle = ticketTitle;
        }

        public BigDecimal getTicketPrice() {
            return ticketPrice;
        }

        public void setTicketPrice(BigDecimal ticketPrice) {
            this.ticketPrice = ticketPrice;
        }

        public String getClassId() {
            return classId;
        }

        public void setClassId(String classId) {
            this.classId = classId;
        }

        public String getClassDesignation() {
            return classDesignation;
        }

        public void setClassDesignation(String classDesignation) {
            this.classDesignation = classDesignation;
        }
    }
}