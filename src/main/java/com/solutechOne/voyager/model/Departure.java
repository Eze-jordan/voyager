package com.solutechOne.voyager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(
        name = "travel_departure",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_company_departure_ref", columnNames = {"company_id", "departure_reference"})
        },
        indexes = {
                @Index(name = "idx_departure_company", columnList = "company_id"),
                @Index(name = "idx_departure_means", columnList = "means_id"),
                @Index(name = "idx_departure_city", columnList = "departure_city_id"),
                @Index(name = "idx_departure_date", columnList = "departure_date"),
                @Index(name = "idx_departure_status", columnList = "departure_status")
        }
)
public class Departure {

    @Id
    @Column(name = "departure_id", length = 60)
    private String departureId;

    @Column(name = "departure_reference", nullable = false, length = 50)
    private String departureReference;

    // ✅ Company
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // ✅ Means
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "means_id", nullable = false)
    private TransportMeans means;

    // ✅ Departure city
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departure_city_id", nullable = false)
    private City departureCity;

    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;

    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    // ✅ Boarding place
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departure_boarding_place_id", nullable = false)
    private Place departureBoardingPlace;

    @Column(name = "departure_checkin_start", nullable = false)
    private LocalTime departureCheckinStart;

    @Column(name = "departure_checkin_end", nullable = false)
    private LocalTime departureCheckinEnd;

    @Column(name = "departure_boarding_time", nullable = false)
    private LocalTime departureBoardingTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "departure_status", nullable = false, length = 20)
    private DepartureStatus departureStatus = DepartureStatus.PROGRAMME;

    public enum DepartureStatus { PROGRAMME, ANNULE, SUSPENDU }


    @Transient
    private String companyId;

    @Transient
    private String meansId;

    @Transient
    private String departureCityId;

    @Transient
    private String departureBoardingPlaceId;
    @PrePersist
    public void generateIdIfNull() {
        if (this.departureId == null) {
            this.departureId = "departure-" + UUID.randomUUID();
        }
        if (this.departureStatus == null) {
            this.departureStatus = DepartureStatus.PROGRAMME;
        }
    }

    // ===== GETTERS & SETTERS =====

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setMeansId(String meansId) {
        this.meansId = meansId;
    }

    public void setDepartureCityId(String departureCityId) {
        this.departureCityId = departureCityId;
    }

    public void setDepartureBoardingPlaceId(String departureBoardingPlaceId) {
        this.departureBoardingPlaceId = departureBoardingPlaceId;
    }

    public String getDepartureId() { return departureId; }
    public void setDepartureId(String departureId) { this.departureId = departureId; }

    public String getDepartureReference() { return departureReference; }
    public void setDepartureReference(String departureReference) { this.departureReference = departureReference; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public TransportMeans getMeans() { return means; }
    public void setMeans(TransportMeans means) { this.means = means; }

    public City getDepartureCity() { return departureCity; }
    public void setDepartureCity(City departureCity) { this.departureCity = departureCity; }

    public LocalDate getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDate departureDate) { this.departureDate = departureDate; }

    public LocalTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalTime departureTime) { this.departureTime = departureTime; }

    public Place getDepartureBoardingPlace() { return departureBoardingPlace; }
    public void setDepartureBoardingPlace(Place departureBoardingPlace) { this.departureBoardingPlace = departureBoardingPlace; }

    public LocalTime getDepartureCheckinStart() { return departureCheckinStart; }
    public void setDepartureCheckinStart(LocalTime departureCheckinStart) { this.departureCheckinStart = departureCheckinStart; }

    public LocalTime getDepartureCheckinEnd() { return departureCheckinEnd; }
    public void setDepartureCheckinEnd(LocalTime departureCheckinEnd) { this.departureCheckinEnd = departureCheckinEnd; }

    public LocalTime getDepartureBoardingTime() { return departureBoardingTime; }
    public void setDepartureBoardingTime(LocalTime departureBoardingTime) { this.departureBoardingTime = departureBoardingTime; }

    public DepartureStatus getDepartureStatus() { return departureStatus; }
    public void setDepartureStatus(DepartureStatus departureStatus) { this.departureStatus = departureStatus; }

    // ✅ IDs exposés sans DTO
    @Transient public String getCompanyId() { return company != null ? company.getCompanyId() : null; }
    @Transient public String getMeansId() { return means != null ? means.getMeansId() : null; }
    @Transient public String getDepartureCityId() { return departureCity != null ? departureCity.getCityId() : null; }
    @Transient public String getDepartureBoardingPlaceId() { return departureBoardingPlace != null ? departureBoardingPlace.getPlaceId() : null; }
}