package com.solutechOne.voyager.model;

import com.solutechOne.voyager.enums.BasketStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "baskets")
public class Basket {

    @Id
    @Column(name = "basket_id", nullable = false, length = 60, updatable = false)
    private String basketId;

    // =========================
    // RELATION COMPANY
    // =========================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // =========================
    // BASKET DATA
    // =========================

    @Column(name = "basket_amount", nullable = false)
    private BigDecimal basketAmount;

    @Column(name = "basket_fees", nullable = false)
    private BigDecimal basketFees;

    @Column(name = "basket_total_amount", nullable = false)
    private BigDecimal basketTotalAmount;

    // =========================
    // PAYMENT
    // =========================

    @Column(name = "basket_payment_service", length = 20)
    private String basketPaymentService;

    @Column(name = "basket_payment_id", length = 20)
    private String basketPaymentId;

    @Column(name = "basket_payment_date")
    private LocalDateTime basketPaymentDate;

    @Column(name = "basket_payment_account", length = 9)
    private String basketPaymentAccount;

    // =========================
    // STATUS
    // =========================

    @Enumerated(EnumType.STRING)
    @Column(name = "basket_status", nullable = false)
    private BasketStatus basketStatus;

    @OneToMany(mappedBy = "basket")
    private List<Travel> travels;
    // =========================
    // AUTO GENERATION
    // =========================

    @PrePersist
    public void generateId() {

        if (this.basketId == null) {
            this.basketId = "basket-" + UUID.randomUUID();
        }

        if (this.basketStatus == null) {
            this.basketStatus = BasketStatus.EN_COURS;
        }

        if (this.basketAmount == null) {
            this.basketAmount = BigDecimal.ZERO;
        }

        if (this.basketFees == null) {
            this.basketFees = BigDecimal.ZERO;
        }

        if (this.basketTotalAmount == null) {
            this.basketTotalAmount = BigDecimal.ZERO;
        }
    }

    // =========================
    // GETTERS / SETTERS
    // =========================

    public String getBasketId() {
        return basketId;
    }

    public Company getCompany() {
        return company;
    }

    public void setBasketId(String basketId) {
        this.basketId = basketId;
    }

    public List<Travel> getTravels() {
        return travels;
    }

    public void setTravels(List<Travel> travels) {
        this.travels = travels;
    }

    public void setCompany(Company company) {
        this.company = company;
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