package com.solutechOne.voyager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solutechOne.voyager.enums.TicketStatus;
import com.solutechOne.voyager.enums.YesNo;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "ticket_price",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_ticket_price_scope",
                        columnNames = {"company_id", "class_id", "departure_city_id", "arrival_city_id", "ticket_title"}
                )
        },
        indexes = {
                @Index(name = "idx_ticket_company", columnList = "company_id"),
                @Index(name = "idx_ticket_route", columnList = "departure_city_id,arrival_city_id"),
                @Index(name = "idx_ticket_class", columnList = "class_id"),
                @Index(name = "idx_ticket_status", columnList = "ticket_status")
        }
)
public class TicketPrice {

    @Id
    @Column(name = "price_id", nullable = false, length = 60, updatable = false)
    private String priceId;

    // ✅ Relations (DB)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_id", nullable = false)
    private TravelClass travelClass;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departure_city_id", nullable = false)
    private City departureCity;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "arrival_city_id", nullable = false)
    private City arrivalCity;

    // ✅ Champs métier
    @Column(name = "ticket_title", nullable = false, length = 30)
    private String ticketTitle;

    @Column(name = "ticket_description", nullable = false, length = 100)
    private String ticketDescription;

    @Column(name = "ticket_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal ticketPrice;

    @Column(name = "ticket_age_mini", nullable = false)
    private Integer ticketAgeMini;

    @Column(name = "ticket_age_maxi", nullable = false)
    private Integer ticketAgeMaxi;

    @Column(name = "ticket_promo_rate")
    private Integer ticketPromoRate;

    @Column(name = "ticket_promo_start")
    private LocalDate ticketPromoStart;

    @Column(name = "ticket_promo_end")
    private LocalDate ticketPromoEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_promo_apply", nullable = false, length = 5)
    private YesNo ticketPromoApply = YesNo.NON;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_seat_authorized", nullable = false, length = 5)
    private YesNo ticketSeatAuthorized;

    @Column(name = "ticket_bag_authorized", nullable = false)
    private Integer ticketBagAuthorized;

    @Column(name = "ticket_height_maxi", nullable = false)
    private Integer ticketHeightMaxi;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_status", nullable = false, length = 20)
    private TicketStatus ticketStatus = TicketStatus.ACTIF; // adapte si ton enum diffère

    // =========================
    // IDs pour le BODY (pas DB)
    // =========================
    @Transient private String companyId;
    @Transient private String classId;
    @Transient private String departureCityId;
    @Transient private String arrivalCityId;

    @PrePersist
    public void generateId() {
        if (this.priceId == null) {
            this.priceId = "price-" + UUID.randomUUID();
        }
        if (this.ticketPromoApply == null) this.ticketPromoApply = YesNo.NON;
        if (this.ticketStatus == null) this.ticketStatus = TicketStatus.ACTIF;
    }

    // ===== Getters & Setters (principaux) =====

    public String getPriceId() { return priceId; }
    public void setPriceId(String priceId) { this.priceId = priceId; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public TravelClass getTravelClass() { return travelClass; }
    public void setTravelClass(TravelClass travelClass) { this.travelClass = travelClass; }

    public City getDepartureCity() { return departureCity; }
    public void setDepartureCity(City departureCity) { this.departureCity = departureCity; }

    public City getArrivalCity() { return arrivalCity; }
    public void setArrivalCity(City arrivalCity) { this.arrivalCity = arrivalCity; }

    // Body IDs
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }

    public String getDepartureCityId() { return departureCityId; }
    public void setDepartureCityId(String departureCityId) { this.departureCityId = departureCityId; }

    public String getArrivalCityId() { return arrivalCityId; }
    public void setArrivalCityId(String arrivalCityId) { this.arrivalCityId = arrivalCityId; }

    // Out IDs (sans DTO)
    @Transient public String getCompanyIdOut() { return company != null ? company.getCompanyId() : null; }
    @Transient public String getClassIdOut() { return travelClass != null ? travelClass.getClassId() : null; }
    @Transient public String getDepartureCityIdOut() { return departureCity != null ? departureCity.getCityId() : null; }
    @Transient public String getArrivalCityIdOut() { return arrivalCity != null ? arrivalCity.getCityId() : null; }

    public String getTicketTitle() {
        return ticketTitle;
    }

    public void setTicketTitle(String ticketTitle) {
        this.ticketTitle = ticketTitle;
    }

    public String getTicketDescription() {
        return ticketDescription;
    }

    public void setTicketDescription(String ticketDescription) {
        this.ticketDescription = ticketDescription;
    }

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public Integer getTicketAgeMini() {
        return ticketAgeMini;
    }

    public void setTicketAgeMini(Integer ticketAgeMini) {
        this.ticketAgeMini = ticketAgeMini;
    }

    public Integer getTicketAgeMaxi() {
        return ticketAgeMaxi;
    }

    public void setTicketAgeMaxi(Integer ticketAgeMaxi) {
        this.ticketAgeMaxi = ticketAgeMaxi;
    }

    public Integer getTicketPromoRate() {
        return ticketPromoRate;
    }

    public void setTicketPromoRate(Integer ticketPromoRate) {
        this.ticketPromoRate = ticketPromoRate;
    }

    public LocalDate getTicketPromoStart() {
        return ticketPromoStart;
    }

    public void setTicketPromoStart(LocalDate ticketPromoStart) {
        this.ticketPromoStart = ticketPromoStart;
    }

    public LocalDate getTicketPromoEnd() {
        return ticketPromoEnd;
    }

    public void setTicketPromoEnd(LocalDate ticketPromoEnd) {
        this.ticketPromoEnd = ticketPromoEnd;
    }

    public YesNo getTicketPromoApply() {
        return ticketPromoApply;
    }

    public void setTicketPromoApply(YesNo ticketPromoApply) {
        this.ticketPromoApply = ticketPromoApply;
    }

    public YesNo getTicketSeatAuthorized() {
        return ticketSeatAuthorized;
    }

    public void setTicketSeatAuthorized(YesNo ticketSeatAuthorized) {
        this.ticketSeatAuthorized = ticketSeatAuthorized;
    }

    public Integer getTicketBagAuthorized() {
        return ticketBagAuthorized;
    }

    public void setTicketBagAuthorized(Integer ticketBagAuthorized) {
        this.ticketBagAuthorized = ticketBagAuthorized;
    }

    public Integer getTicketHeightMaxi() {
        return ticketHeightMaxi;
    }

    public void setTicketHeightMaxi(Integer ticketHeightMaxi) {
        this.ticketHeightMaxi = ticketHeightMaxi;
    }

    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    // ... garde tes autres getters/setters (title, price, promo, etc.)
}