package com.solutechOne.voyager.dto.cashier;

public class OpenCashierBasketRequest {

    private String buyerPhone;
    private String buyerWhatsapp;
    private String buyerEmail;

    public OpenCashierBasketRequest() {
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public String getBuyerWhatsapp() {
        return buyerWhatsapp;
    }

    public void setBuyerWhatsapp(String buyerWhatsapp) {
        this.buyerWhatsapp = buyerWhatsapp;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }
}