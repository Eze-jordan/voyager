package com.solutechOne.voyager.dto;

import java.math.BigDecimal;

public class PaymentInitVoyagerResponse {

    private String paymentId;
    private String basketId;
    private String reference;
    private String status;
    private BigDecimal amount;
    private String externalTransactionId;
    private String message;

    public PaymentInitVoyagerResponse() {
    }

    public PaymentInitVoyagerResponse(String paymentId,
                                      String basketId,
                                      String reference,
                                      String status,
                                      BigDecimal amount,
                                      String externalTransactionId,
                                      String message) {
        this.paymentId = paymentId;
        this.basketId = basketId;
        this.reference = reference;
        this.status = status;
        this.amount = amount;
        this.externalTransactionId = externalTransactionId;
        this.message = message;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getBasketId() {
        return basketId;
    }

    public void setBasketId(String basketId) {
        this.basketId = basketId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getExternalTransactionId() {
        return externalTransactionId;
    }

    public void setExternalTransactionId(String externalTransactionId) {
        this.externalTransactionId = externalTransactionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}