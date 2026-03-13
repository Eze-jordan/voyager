package com.solutechOne.voyager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(
        name = "cities",
        uniqueConstraints = @UniqueConstraint(columnNames = {"company_id", "city_name"})
)
public class City {

    @Id
    @Column(name = "city_id", length = 60, nullable = false, updatable = false)
    private String cityId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "city_name", length = 100, nullable = false)
    private String cityName;

    public City() {}

    public City(Company company, String cityName) {
        this.company = company;
        this.cityName = cityName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    // ✅ Naming correct
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}