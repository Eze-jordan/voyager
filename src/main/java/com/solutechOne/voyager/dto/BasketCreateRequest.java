package com.solutechOne.voyager.dto;

public class BasketCreateRequest {
    public String companyId;
    public String buyerPhone;
    public String buyerWhatsapp;
    public String buyerEmail;

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
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