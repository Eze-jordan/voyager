package com.solutechOne.voyager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(
        name = "places",
        uniqueConstraints = @UniqueConstraint(columnNames = {"company_id", "city_id", "place_name"}),
        indexes = {
                @Index(name = "idx_places_company", columnList = "company_id"),
                @Index(name = "idx_places_city", columnList = "city_id")
        }
)
public class Place {

    @Id
    @Column(name = "place_id", length = 60, nullable = false, updatable = false)
    private String placeId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(name = "place_name", length = 100, nullable = false)
    private String placeName;

    @Column(name = "place_address", length = 200)
    private String placeAddress;

    @Column(name = "place_gps_longitude", length = 30)
    private String placeGpsLongitude;

    @Column(name = "place_gps_latitude", length = 30)
    private String placeGpsLatitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "place_status", length = 10, nullable = false)
    private PlaceStatus placeStatus = PlaceStatus.ACTIF;

    public enum PlaceStatus {
        ACTIF,
        SUSPENDU
    }

    public Place() {}

    public Place(Company company, City city, String placeName, String placeAddress,
                 String placeGpsLongitude, String placeGpsLatitude, PlaceStatus placeStatus) {
        this.company = company;
        this.city = city;
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.placeGpsLongitude = placeGpsLongitude;
        this.placeGpsLatitude = placeGpsLatitude;
        this.placeStatus = placeStatus;
    }

    public String getPlaceId() { return placeId; }
    public void setPlaceId(String placeId) { this.placeId = placeId; }

    // ✅ naming correct
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }

    public String getPlaceName() { return placeName; }
    public void setPlaceName(String placeName) { this.placeName = placeName; }

    public String getPlaceAddress() { return placeAddress; }
    public void setPlaceAddress(String placeAddress) { this.placeAddress = placeAddress; }

    public String getPlaceGpsLongitude() { return placeGpsLongitude; }
    public void setPlaceGpsLongitude(String placeGpsLongitude) { this.placeGpsLongitude = placeGpsLongitude; }

    public String getPlaceGpsLatitude() { return placeGpsLatitude; }
    public void setPlaceGpsLatitude(String placeGpsLatitude) { this.placeGpsLatitude = placeGpsLatitude; }

    public PlaceStatus getPlaceStatus() { return placeStatus; }
    public void setPlaceStatus(PlaceStatus placeStatus) { this.placeStatus = placeStatus; }
}