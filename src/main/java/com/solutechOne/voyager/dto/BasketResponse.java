package com.solutechOne.voyager.dto;

import com.solutechOne.voyager.model.Basket;
import com.solutechOne.voyager.enums.BasketStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BasketResponse {

    public String basketId;
    public String companyId;

    public BigDecimal basketAmount;
    public BigDecimal basketFees;
    public BigDecimal basketTotalAmount;

    public String basketPaymentService;
    public String basketPaymentId;
    public LocalDateTime basketPaymentDate;
    public String basketPaymentAccount;

    public BasketStatus basketStatus;

    public static BasketResponse fromEntity(Basket b) {
        BasketResponse r = new BasketResponse();
        r.basketId = b.getBasketId();
        r.companyId = b.getCompany() != null ? b.getCompany().getCompanyId() : null;

        r.basketAmount = b.getBasketAmount();
        r.basketFees = b.getBasketFees();
        r.basketTotalAmount = b.getBasketTotalAmount();

        r.basketPaymentService = b.getBasketPaymentService();
        r.basketPaymentId = b.getBasketPaymentId();
        r.basketPaymentDate = b.getBasketPaymentDate();
        r.basketPaymentAccount = b.getBasketPaymentAccount();

        r.basketStatus = b.getBasketStatus();
        return r;

    }

    public String getBasketId() {
        return basketId;
    }

    public void setBasketId(String basketId) {
        this.basketId = basketId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public BigDecimal getBasketAmount() {
        return basketAmount;
    }

    public void setBasketAmount(BigDecimal basketAmount) {
        this.basketAmount = basketAmount;
    }

    public BigDecimal getBasketFees() {
        return basketFees;
    }

    public void setBasketFees(BigDecimal basketFees) {
        this.basketFees = basketFees;
    }

    public BigDecimal getBasketTotalAmount() {
        return basketTotalAmount;
    }

    public void setBasketTotalAmount(BigDecimal basketTotalAmount) {
        this.basketTotalAmount = basketTotalAmount;
    }

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

    public LocalDateTime getBasketPaymentDate() {
        return basketPaymentDate;
    }

    public void setBasketPaymentDate(LocalDateTime basketPaymentDate) {
        this.basketPaymentDate = basketPaymentDate;
    }

    public String getBasketPaymentAccount() {
        return basketPaymentAccount;
    }

    public void setBasketPaymentAccount(String basketPaymentAccount) {
        this.basketPaymentAccount = basketPaymentAccount;
    }

    public BasketStatus getBasketStatus() {
        return basketStatus;
    }

    public void setBasketStatus(BasketStatus basketStatus) {
        this.basketStatus = basketStatus;
    }
}