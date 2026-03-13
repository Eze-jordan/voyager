package com.solutechOne.voyager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(
        name = "classes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"company_id", "class_designation"})
        },
        indexes = {
                @Index(name = "idx_classes_company", columnList = "company_id")
        }
)
public class TravelClass {

    @Id
    @Column(name = "class_id", length = 50)
    private String classId;

    // ✅ relation : plusieurs classes appartiennent à une compagnie
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "class_designation", length = 50, nullable = false)
    private String classDesignation;

    @Enumerated(EnumType.STRING)
    @Column(name = "class_status", length = 10, nullable = false)
    private ClassStatus classStatus = ClassStatus.ACTIF;

    public enum ClassStatus {
        ACTIF,
        SUSPENDU
    }

    public TravelClass() {}

    public TravelClass(Company company, String classDesignation) {
        this.company = company;
        this.classDesignation = classDesignation;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getClassDesignation() {
        return classDesignation;
    }

    public void setClassDesignation(String classDesignation) {
        this.classDesignation = classDesignation;
    }

    public ClassStatus getClassStatus() {
        return classStatus;
    }

    public void setClassStatus(ClassStatus classStatus) {
        this.classStatus = classStatus;
    }
}