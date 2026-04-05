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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "basket_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal basketAmount;

    @Column(name = "basket_fees", nullable = false, precision = 15, scale = 2)
    private BigDecimal basketFees;

    @Column(name = "basket_total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal basketTotalAmount;

    @Column(name = "basket_payment_service", length = 20)
    private String basketPaymentService;

    @Column(name = "basket_payment_id", length = 50)
    private String basketPaymentId;

    @Column(name = "basket_payment_date")
    private LocalDateTime basketPaymentDate;

    @Column(name = "basket_payment_account", length = 20)
    private String basketPaymentAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "basket_status", nullable = false, length = 20)
    private BasketStatus basketStatus;

    @OneToMany(mappedBy = "basket")
    private List<Travel> travels;

    @Column(name = "guest_uuid", nullable = false, unique = true, length = 60, updatable = false)
    private String guestUuid;

    @Column(name = "buyer_phone", length = 20)
    private String buyerPhone;

    @Column(name = "buyer_whatsapp", length = 20)
    private String buyerWhatsapp;

    @Column(name = "buyer_email", length = 100)
    private String buyerEmail;

    @Column(name = "number_of_reservations", nullable = false)
    private Integer numberOfReservations;

    @OneToMany(mappedBy = "basket")
    private List<Reservation> reservations;
    @PrePersist
    public void prePersist() {
        if (this.basketId == null || this.basketId.isBlank()) {
            this.basketId = "basket-" + UUID.randomUUID();
        }

        if (this.guestUuid == null || this.guestUuid.isBlank()) {
            this.guestUuid = "guest-" + UUID.randomUUID().toString();
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

        if (this.numberOfReservations == null) {
            this.numberOfReservations = 0;
        }
    }
    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public String getBasketId() {
        return basketId;
    }

    public void setBasketId(String basketId) {
        this.basketId = basketId;
    }

    public Company getCompany() {
        return company;
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

    public List<Travel> getTravels() {
        return travels;
    }

    public void setTravels(List<Travel> travels) {
        this.travels = travels;
    }

    public String getGuestUuid() {
        return guestUuid;
    }

    public void setGuestUuid(String guestUuid) {
        this.guestUuid = guestUuid;
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

    public Integer getNumberOfReservations() {
        return numberOfReservations;
    }

    public void setNumberOfReservations(Integer numberOfReservations) {
        this.numberOfReservations = numberOfReservations;
    }
}