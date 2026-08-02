package com.solutechOne.voyager.dto;

import com.solutechOne.voyager.enums.StatutTicket;

/**
 * Données utilisées par un administrateur pour traiter un ticket.
 */
public class UpdateTicketStatusRequest {

    private StatutTicket statut;
    private String reponseAdmin;

    public UpdateTicketStatusRequest() {
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
}