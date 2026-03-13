package com.solutechOne.voyager.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class DepartureCreateRequest {
    public String companyId;
    public String meansId;
    public String departureCityId;
    public String departureBoardingPlaceId;
    public String departureReference;
    public LocalDate departureDate;
    public LocalTime departureTime;
    public LocalTime departureCheckinStart;
    public LocalTime departureCheckinEnd;
    public LocalTime departureBoardingTime;
    public String departureStatus;

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getMeansId() {
        return meansId;
    }

    public void setMeansId(String meansId) {
        this.meansId = meansId;
    }

    public String getDepartureCityId() {
        return departureCityId;
    }

    public void setDepartureCityId(String departureCityId) {
        this.departureCityId = departureCityId;
    }

    public String getDepartureBoardingPlaceId() {
        return departureBoardingPlaceId;
    }

    public void setDepartureBoardingPlaceId(String departureBoardingPlaceId) {
        this.departureBoardingPlaceId = departureBoardingPlaceId;
    }

    public String getDepartureReference() {
        return departureReference;
    }

    public void setDepartureReference(String departureReference) {
        this.departureReference = departureReference;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalTime getDepartureCheckinStart() {
        return departureCheckinStart;
    }

    public void setDepartureCheckinStart(LocalTime departureCheckinStart) {
        this.departureCheckinStart = departureCheckinStart;
    }

    public LocalTime getDepartureCheckinEnd() {
        return departureCheckinEnd;
    }

    public void setDepartureCheckinEnd(LocalTime departureCheckinEnd) {
        this.departureCheckinEnd = departureCheckinEnd;
    }

    public LocalTime getDepartureBoardingTime() {
        return departureBoardingTime;
    }

    public void setDepartureBoardingTime(LocalTime departureBoardingTime) {
        this.departureBoardingTime = departureBoardingTime;
    }

    public String getDepartureStatus() {
        return departureStatus;
    }

    public void setDepartureStatus(String departureStatus) {
        this.departureStatus = departureStatus;
    }
}