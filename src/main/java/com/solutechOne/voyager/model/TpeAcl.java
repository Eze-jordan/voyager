package com.solutechOne.voyager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solutechOne.voyager.enums.AclStatus;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "tpe_acl")
public class TpeAcl {

    @Id
    @Column(name = "acl_id", nullable = false, updatable = false)
    private String aclId;

    // 🔥 relation compagnie
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Transient
    private String companyId;

    // 🔥 relation TPE
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tpe_id", nullable = false)
    private Tpe tpe;

    @Transient
    private String tpeId;

    // 🔥 lieu externe
    @Column(name = "place_id", nullable = false)
    private String placeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "acl_status", nullable = false)
    private AclStatus aclStatus;

    @PrePersist
    public void generateId() {
        if (this.aclId == null) {
            this.aclId = "acl-" + UUID.randomUUID();
        }

        if (this.aclStatus == null) {
            this.aclStatus = AclStatus.ACTIF;
        }
    }

    // ===== getters =====

    public String getAclId() { return aclId; }

    public Company getCompany() { return company; }

    public String getCompanyId() { return companyId; }

    public Tpe getTpe() { return tpe; }

    public String getTpeId() { return tpeId; }

    public String getPlaceId() { return placeId; }

    public AclStatus getAclStatus() { return aclStatus; }

    // ===== setters =====

    public void setAclId(String aclId) { this.aclId = aclId; }

    public void setCompany(Company company) { this.company = company; }

    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public void setTpe(Tpe tpe) { this.tpe = tpe; }

    public void setTpeId(String tpeId) { this.tpeId = tpeId; }

    public void setPlaceId(String placeId) { this.placeId = placeId; }

    public void setAclStatus(AclStatus aclStatus) { this.aclStatus = aclStatus; }
}