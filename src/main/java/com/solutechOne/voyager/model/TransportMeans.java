package com.solutechOne.voyager.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solutechOne.voyager.enums.SeatOccupationMode;
import com.solutechOne.voyager.enums.TransportMeansStatus;
import jakarta.persistence.*;
@Entity
@Table(
        name = "transport_means",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_company_register_id",
                columnNames = {"company_id", "register_id"}
        ),
        indexes = {
                @Index(name = "idx_transport_means_company", columnList = "company_id"),
                @Index(name = "idx_transport_means_status", columnList = "status")
        }
)
public class TransportMeans {

    @Id
    @Column(name = "means_id", nullable = false, length = 60)
    private String meansId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "transport_type", nullable = false, length = 30)
    private String transportType;

    @Column(name = "type_vehicle", length = 30)
    private String typeVehicle;

    @Column(name = "type_boat", length = 30)
    private String typeBoat;

    @Column(name = "register_id", nullable = false, length = 30)
    private String registerId;

    @Column(name = "brand", nullable = false, length = 50)
    private String brand;

    @Column(name = "model", nullable = false, length = 50)
    private String model;

    @Column(name = "designation", length = 80)
    private String designation;

    @Column(name = "total_seat", nullable = false)
    private Integer totalSeat;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_occupation_mode", nullable = false, length = 20)
    private SeatOccupationMode seatOccupationMode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TransportMeansStatus status = TransportMeansStatus.ACTIF;

    public String getMeansId() { return meansId; }
    public void setMeansId(String meansId) { this.meansId = meansId; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public String getTypeVehicle() {
        return typeVehicle;
    }

    public void setTypeVehicle(String typeVehicle) {
        this.typeVehicle = typeVehicle;
    }

    public String getTypeBoat() {
        return typeBoat;
    }

    public void setTypeBoat(String typeBoat) {
        this.typeBoat = typeBoat;
    }

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
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

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Integer getTotalSeat() {
        return totalSeat;
    }

    public void setTotalSeat(Integer totalSeat) {
        this.totalSeat = totalSeat;
    }

    public SeatOccupationMode getSeatOccupationMode() {
        return seatOccupationMode;
    }

    public void setSeatOccupationMode(SeatOccupationMode seatOccupationMode) {
        this.seatOccupationMode = seatOccupationMode;
    }

    public TransportMeansStatus getStatus() {
        return status;
    }

    public void setStatus(TransportMeansStatus status) {
        this.status = status;
    }
}