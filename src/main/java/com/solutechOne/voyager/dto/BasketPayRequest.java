package com.solutechOne.voyager.dto;

public class BasketPayRequest {
    public String basketPaymentService;
    public String basketPaymentId;
    public String basketPaymentAccount;

    public String getBasketPaymentService() {
        return basketPaymentService;
    }

    public void setBasketPaymentService(String basketPaymentService) {
        this.basketPaymentService = basketPaymentService;
    }

    public String getBasketPaymentId() {
        return basketPaymentId;
    }

    public void setBasketPaymentId(String basketPaymentId) {
        this.basketPaymentId = basketPaymentId;
    }

    public String getBasketPaymentAccount() {
        return basketPaymentAccount;
    }

    public void setBasketPaymentAccount(String basketPaymentAccount) {
        this.basketPaymentAccount = basketPaymentAccount;
    }
}