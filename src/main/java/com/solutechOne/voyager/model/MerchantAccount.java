package com.solutechOne.voyager.model;

import com.solutechOne.voyager.enums.PaymentProvider;
import com.solutechOne.voyager.enums.Status;
import jakarta.persistence.*;

@Entity
@Table(name = "merchant_accounts")
public class MerchantAccount {

    @Id
    @Column(name = "merchant_id", nullable = false, length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Company company;

    @Enumerated(EnumType.STRING)
    private PaymentProvider provider;

    @Column(nullable = false)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public PaymentProvider getProvider() {
        return provider;
    }

    public void setProvider(PaymentProvider provider) {
        this.provider = provider;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
