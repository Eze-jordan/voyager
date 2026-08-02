package com.solutechOne.voyager.model;

import com.solutechOne.voyager.enums.StatutTicket;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "tickets",
        indexes = {
                @Index(
                        name = "idx_ticket_company_id",
                        columnList = "company_id"
                ),
                @Index(
                        name = "idx_ticket_statut",
                        columnList = "statut"
                ),
                @Index(
                        name = "idx_ticket_created_at",
                        columnList = "created_at"
                )
        }
)
public class Ticket {

    /**
     * Exemple :
     * ticket-6f86c75e-c1a4-4e4d-94f3-dc06156ef13e
     */
    @Id
    @Column(
            name = "ticket_id",
            nullable = false,
            updatable = false,
            unique = true,
            length = 50
    )
    private String id;

    /**
     * Compagnie ayant créé le ticket.
     *
     * Un ticket ne peut pas exister sans compagnie.
     */
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "company_id",
            nullable = false
    )
    private Company company;

    @Column(
            name = "titre",
            nullable = false,
            length = 255
    )
    private String titre;

    @Column(
            name = "description",
            nullable = false,
            length = 4000
    )
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "statut",
            nullable = false,
            length = 30
    )
    private StatutTicket statut = StatutTicket.OUVERT;

    @Column(
            name = "reponse_admin",
            length = 4000
    )
    private String reponseAdmin;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        if (id == null || id.isBlank()) {
            id = "ticket-" + UUID.randomUUID();
        }

        LocalDateTime maintenant = LocalDateTime.now();

        createdAt = maintenant;
        updatedAt = maintenant;

        if (statut == null) {
            statut = StatutTicket.OUVERT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatutTicket getStatut() {
        return statut;
    }

    public void setStatut(StatutTicket statut) {
        this.statut = statut;
    }

    public String getReponseAdmin() {
        return reponseAdmin;
    }

    public void setReponseAdmin(String reponseAdmin) {
        this.reponseAdmin = reponseAdmin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}