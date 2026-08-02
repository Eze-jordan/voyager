package com.solutechOne.voyager.dto;

/**
 * Données envoyées par une compagnie pour créer un ticket.
 */
public class CreateTicketRequest {

    private String titre;
    private String description;

    public CreateTicketRequest() {
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
}