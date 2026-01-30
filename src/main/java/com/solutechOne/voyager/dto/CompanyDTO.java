package com.solutechOne.voyager.dto;

import com.solutechOne.voyager.enums.CompanyStatus;
import com.solutechOne.voyager.enums.CompanyType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class CompanyDTO {

    private String id;
    private CompanyType type;
    private String name;
    private String urlLogo;
    private String nif;
    private String rccm;
    private String appId;
    private String address;
    private Long phone;
    private String email;
    private String aggrement;
    private BigDecimal rateFees;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private CompanyStatus status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CompanyType getType() {
        return type;
    }

    public void setType(CompanyType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlLogo() {
        return urlLogo;
    }

    public void setUrlLogo(String urlLogo) {
        this.urlLogo = urlLogo;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getRccm() {
        return rccm;
    }

    public void setRccm(String rccm) {
        this.rccm = rccm;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAggrement() {
        return aggrement;
    }

    public void setAggrement(String aggrement) {
        this.aggrement = aggrement;
    }

    public BigDecimal getRateFees() {
        return rateFees;
    }

    public void setRateFees(BigDecimal rateFees) {
        this.rateFees = rateFees;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public CompanyStatus getStatus() {
        return status;
    }

    public void setStatus(CompanyStatus status) {
        this.status = status;
    }

    // getters / setters
}
