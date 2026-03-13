package com.solutechOne.voyager.dto;

import java.math.BigDecimal;

public class BasketAmountRequest {
    public BigDecimal basketAmount;

    public BigDecimal getBasketAmount() {
        return basketAmount;
    }

    public void setBasketAmount(BigDecimal basketAmount) {
        this.basketAmount = basketAmount;
    }
}