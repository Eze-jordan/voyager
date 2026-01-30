package com.solutechOne.voyager.dto;


import com.solutechOne.voyager.enums.CompanyType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class CompanyCreateRequest {

    @NotNull
    private CompanyType type;

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String urlLogo;

    @Size(max = 12)
    private String nif;

    @Size(max = 15)
    private String rccm;

    @Size(max = 10)
    private String appId;

    @Size(max = 50)
    private String address;

    // garder String pour téléphone (préserve les zéros, +241 etc.)
    @Pattern(regexp = "^[0-20]{20}$", message = "Le téléphone doit contenir 9 chiffres")
    private String phone;

    @NotBlank
    @Email
    @Size(max = 50)
    private String email;
    @Column(nullable = false)
    private String motDePasse;
    @NotBlank
    @Size(max = 100)
    private String aggrement;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal rateFees;

    public @NotNull CompanyType getType() {
        return type;
    }

    public void setType(@NotNull CompanyType type) {
        this.type = type;
    }

    public @NotBlank @Size(max = 50) String getName() {
        return name;
    }

    public void setName(@NotBlank @Size(max = 50) String name) {
        this.name = name;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public @NotBlank @Size(max = 100) String getUrlLogo() {
        return urlLogo;
    }

    public void setUrlLogo(@NotBlank @Size(max = 100) String urlLogo) {
        this.urlLogo = urlLogo;
    }

    public @Size(max = 12) String getNif() {
        return nif;
    }

    public void setNif(@Size(max = 12) String nif) {
        this.nif = nif;
    }

    public @Size(max = 15) String getRccm() {
        return rccm;
    }

    public void setRccm(@Size(max = 15) String rccm) {
        this.rccm = rccm;
    }

    public @Size(max = 10) String getAppId() {
        return appId;
    }

    public void setAppId(@Size(max = 10) String appId) {
        this.appId = appId;
    }

    public @Size(max = 50) String getAddress() {
        return address;
    }

    public void setAddress(@Size(max = 50) String address) {
        this.address = address;
    }

    public @Pattern(regexp = "^[0-9]{9}$", message = "Le téléphone doit contenir 9 chiffres") String getPhone() {
        return phone;
    }

    public void setPhone(@Pattern(regexp = "^[0-9]{9}$", message = "Le téléphone doit contenir 9 chiffres") String phone) {
        this.phone = phone;
    }

    public @NotBlank @Email @Size(max = 50) String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank @Email @Size(max = 50) String email) {
        this.email = email;
    }

    public @NotBlank @Size(max = 100) String getAggrement() {
        return aggrement;
    }

    public void setAggrement(@NotBlank @Size(max = 100) String aggrement) {
        this.aggrement = aggrement;
    }

    public @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal getRateFees() {
        return rateFees;
    }

    public void setRateFees(@NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal rateFees) {
        this.rateFees = rateFees;
    }
}