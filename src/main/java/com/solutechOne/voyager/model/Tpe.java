package com.solutechOne.voyager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solutechOne.voyager.enums.TpeStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tpe")
public class Tpe {

    @Id
    @Column(name = "tpe_id", length = 20, nullable = false, updatable = false)
    private String tpeId;

    // 🔥 RELATION AVEC COMPANY
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // 🔥 pour recevoir depuis le body
    @Transient
    private String companyId;

    @Column(name = "tpe_brand", length = 20, nullable = false)
    private String tpeBrand;

    @Column(name = "tpe_model", length = 20, nullable = false)
    private String tpeModel;

    @Column(name = "tpe_master_key", length = 10, nullable = false)
    private String tpeMasterKey;

    @Column(name = "tpe_last_conx")
    private LocalDateTime tpeLastConx;

    @Enumerated(EnumType.STRING)
    @Column(name = "tpe_status", nullable = false)
    private TpeStatus tpeStatus;

    // 🔥 génération auto
    @PrePersist
    public void generateData() {
        if (this.tpeId == null) {
            this.tpeId = "tpe-" + UUID.randomUUID().toString().substring(0, 8);
        }

        if (this.tpeMasterKey == null) {
            this.tpeMasterKey = UUID.randomUUID().toString().replace("-", "")
                    .substring(0, 10).toUpperCase();
        }

        if (this.tpeStatus == null) {
            this.tpeStatus = TpeStatus.ACTIF;
        }
    }

    // ===== GETTERS =====

    public String getTpeId() { return tpeId; }

    public Company getCompany() { return company; }

    public String getCompanyId() { return companyId; }

    public String getTpeBrand() { return tpeBrand; }

    public String getTpeModel() { return tpeModel; }

    public String getTpeMasterKey() { return tpeMasterKey; }

    public LocalDateTime getTpeLastConx() { return tpeLastConx; }

    public TpeStatus getTpeStatus() { return tpeStatus; }

    // ===== SETTERS =====

    public void setTpeId(String tpeId) { this.tpeId = tpeId; }

    public void setCompany(Company company) { this.company = company; }

    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public void setTpeBrand(String tpeBrand) { this.tpeBrand = tpeBrand; }

    public void setTpeModel(String tpeModel) { this.tpeModel = tpeModel; }

    public void setTpeMasterKey(String tpeMasterKey) { this.tpeMasterKey = tpeMasterKey; }

    public void setTpeLastConx(LocalDateTime tpeLastConx) { this.tpeLastConx = tpeLastConx; }

    public void setTpeStatus(TpeStatus tpeStatus) { this.tpeStatus = tpeStatus; }
}