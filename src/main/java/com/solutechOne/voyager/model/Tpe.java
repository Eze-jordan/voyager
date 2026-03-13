package com.solutechOne.voyager.model;

import com.solutechOne.voyager.enums.Status;
import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "tpe")
public class Tpe {

    @Id
    @Column(name = "tpe_id", nullable = false, length = 20)
    private String id; // Numéro série manuel

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Company company;

    private String brand;
    private String model;

    @Column(nullable = false)
    private String masterKey;

    private LocalDateTime lastConnection;

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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMasterKey() {
        return masterKey;
    }

    public void setMasterKey(String masterKey) {
        this.masterKey = masterKey;
    }

    public LocalDateTime getLastConnection() {
        return lastConnection;
    }

    public void setLastConnection(LocalDateTime lastConnection) {
        this.lastConnection = lastConnection;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

