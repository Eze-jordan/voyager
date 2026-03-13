package com.solutechOne.voyager.dto;

public class PlaceCreateRequest {
    public String companyId;
    public String cityId;
    public String placeName;
    public String placeAddress;
    public String placeGpsLongitude;
    public String placeGpsLatitude;
    public String placeStatus; // "ACTIF" / "SUSPENDU" (optionnel)


    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public String getPlaceGpsLongitude() {
        return placeGpsLongitude;
    }

    public void setPlaceGpsLongitude(String placeGpsLongitude) {
        this.placeGpsLongitude = placeGpsLongitude;
    }

    public String getPlaceGpsLatitude() {
        return placeGpsLatitude;
    }

    public void setPlaceGpsLatitude(String placeGpsLatitude) {
        this.placeGpsLatitude = placeGpsLatitude;
    }

    public String getPlaceStatus() {
        return placeStatus;
    }

    public void setPlaceStatus(String placeStatus) {
        this.placeStatus = placeStatus;
    }
}