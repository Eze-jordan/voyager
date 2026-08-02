package com.solutechOne.voyager.dto.cashier;

public class StartCashierPaymentRequest {

    private String paymentService;
    private String paymentId;
    private String paymentAccount;

    public StartCashierPaymentRequest() {
    }

    public String getPaymentService() {
        return paymentService;
    }

    public void setPaymentService(String paymentService) {
        this.paymentService = paymentService;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentAccount() {
        return paymentAccount;
    }

    public void setPaymentAccount(String paymentAccount) {
        this.paymentAccount = paymentAccount;
    }
}