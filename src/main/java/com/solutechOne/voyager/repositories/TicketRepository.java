package com.solutechOne.voyager.repositories;

import com.solutechOne.voyager.enums.StatutTicket;
import com.solutechOne.voyager.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, String> {

    /**
     * Tous les tickets, du plus récent au plus ancien.
     */
    List<Ticket> findAllByOrderByCreatedAtDesc();

    /**
     * Recherche tous les tickets appartenant à une compagnie.
     *
     * Recherche réalisée depuis Company.companyId.
     */
    List<Ticket> findByCompany_CompanyIdOrderByCreatedAtDesc(
            String companyId
    );

    /**
     * Recherche un ticket précis appartenant à une compagnie précise.
     *
     * Empêche une compagnie de consulter un ticket appartenant
     * à une autre compagnie.
     */
    Optional<Ticket> findByIdAndCompany_CompanyId(
            String ticketId,
            String companyId
    );

    /**
     * Recherche les tickets d'une compagnie selon leur statut.
     */
    List<Ticket> findByCompany_CompanyIdAndStatutOrderByCreatedAtDesc(
            String companyId,
            StatutTicket statut
    );

    /**
     * Recherche tous les tickets ayant un statut précis.
     */
    List<Ticket> findByStatutOrderByCreatedAtDesc(
            StatutTicket statut
    );

    /**
     * Vérifie qu'un ticket appartient bien à une compagnie.
     */
    boolean existsByIdAndCompany_CompanyId(
            String ticketId,
            String companyId
    );
}