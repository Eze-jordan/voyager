package com.solutechOne.voyager.service;

import com.solutechOne.voyager.dto.CreateTicketRequest;
import com.solutechOne.voyager.dto.TicketDTO;
import com.solutechOne.voyager.dto.UpdateTicketStatusRequest;
import com.solutechOne.voyager.enums.StatutTicket;
import com.solutechOne.voyager.model.Company;
import com.solutechOne.voyager.model.Ticket;
import com.solutechOne.voyager.repositories.CompanyRepository;
import com.solutechOne.voyager.repositories.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CompanyRepository companyRepository;

    public TicketService(
            TicketRepository ticketRepository,
            CompanyRepository companyRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.companyRepository = companyRepository;
    }

    /* =====================================================
       CRÉATION PAR UNE COMPAGNIE
       ===================================================== */

    /**
     * Crée un ticket obligatoirement relié à une compagnie.
     */
    public TicketDTO createTicket(
            String companyId,
            CreateTicketRequest request
    ) {
        validateCompanyId(companyId);
        validateCreateRequest(request);

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Compagnie introuvable: " + companyId
                ));

        Ticket ticket = new Ticket();

        /*
         * Ne pas définir l'identifiant ici.
         *
         * Il sera automatiquement généré par Ticket.onCreate()
         * sous la forme :
         * ticket-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
         */
        ticket.setCompany(company);
        ticket.setTitre(request.getTitre().trim());
        ticket.setDescription(request.getDescription().trim());
        ticket.setStatut(StatutTicket.OUVERT);
        ticket.setReponseAdmin(null);

        Ticket savedTicket = ticketRepository.save(ticket);

        return toDto(savedTicket);
    }

    /* =====================================================
       RECHERCHE PAR COMPAGNIE
       ===================================================== */

    /**
     * Retourne tous les tickets d'une compagnie.
     */
    @Transactional(readOnly = true)
    public List<TicketDTO> findByCompany(String companyId) {
        validateCompanyId(companyId);
        verifyCompanyExists(companyId);

        return ticketRepository
                .findByCompany_CompanyIdOrderByCreatedAtDesc(companyId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Recherche un ticket précis dans les tickets d'une compagnie.
     */
    @Transactional(readOnly = true)
    public TicketDTO findByCompanyAndTicketId(
            String companyId,
            String ticketId
    ) {
        validateCompanyId(companyId);
        validateTicketId(ticketId);

        Ticket ticket = ticketRepository
                .findByIdAndCompany_CompanyId(
                        ticketId,
                        companyId
                )
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ticket introuvable pour la compagnie "
                                + companyId
                                + ": "
                                + ticketId
                ));

        return toDto(ticket);
    }

    /**
     * Recherche les tickets d'une compagnie selon leur statut.
     */
    @Transactional(readOnly = true)
    public List<TicketDTO> findByCompanyAndStatut(
            String companyId,
            StatutTicket statut
    ) {
        validateCompanyId(companyId);
        verifyCompanyExists(companyId);

        if (statut == null) {
            throw new IllegalArgumentException(
                    "Le statut est obligatoire"
            );
        }

        return ticketRepository
                .findByCompany_CompanyIdAndStatutOrderByCreatedAtDesc(
                        companyId,
                        statut
                )
                .stream()
                .map(this::toDto)
                .toList();
    }

    /* =====================================================
       CONSULTATION ADMINISTRATEUR
       ===================================================== */

    /**
     * Retourne tous les tickets.
     */
    @Transactional(readOnly = true)
    public List<TicketDTO> findAll() {
        return ticketRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Recherche un ticket depuis son identifiant.
     */
    @Transactional(readOnly = true)
    public TicketDTO findById(String ticketId) {
        validateTicketId(ticketId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ticket introuvable: " + ticketId
                ));

        return toDto(ticket);
    }

    /**
     * Recherche tous les tickets selon leur statut.
     */
    @Transactional(readOnly = true)
    public List<TicketDTO> findByStatut(StatutTicket statut) {
        if (statut == null) {
            throw new IllegalArgumentException(
                    "Le statut est obligatoire"
            );
        }

        return ticketRepository
                .findByStatutOrderByCreatedAtDesc(statut)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /* =====================================================
       TRAITEMENT PAR UN ADMINISTRATEUR
       ===================================================== */

    /**
     * Modifie le statut et la réponse administrateur.
     */
    public TicketDTO updateStatut(
            String ticketId,
            UpdateTicketStatusRequest request
    ) {
        validateTicketId(ticketId);

        if (request == null) {
            throw new IllegalArgumentException(
                    "Les informations de mise à jour sont obligatoires"
            );
        }

        if (request.getStatut() == null) {
            throw new IllegalArgumentException(
                    "Le statut est obligatoire"
            );
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ticket introuvable: " + ticketId
                ));

        ticket.setStatut(request.getStatut());

        if (request.getReponseAdmin() != null) {
            String reponse = request.getReponseAdmin().trim();

            if (reponse.length() > 4000) {
                throw new IllegalArgumentException(
                        "La réponse administrateur ne peut pas dépasser 4000 caractères"
                );
            }

            ticket.setReponseAdmin(
                    reponse.isBlank()
                            ? null
                            : reponse
            );
        }

        Ticket savedTicket = ticketRepository.save(ticket);

        return toDto(savedTicket);
    }

    /**
     * Supprime définitivement un ticket.
     */
    public void delete(String ticketId) {
        validateTicketId(ticketId);

        if (!ticketRepository.existsById(ticketId)) {
            throw new EntityNotFoundException(
                    "Ticket introuvable: " + ticketId
            );
        }

        ticketRepository.deleteById(ticketId);
    }

    /* =====================================================
       VALIDATION
       ===================================================== */

    private void validateCompanyId(String companyId) {
        if (companyId == null || companyId.isBlank()) {
            throw new IllegalArgumentException(
                    "L'identifiant de la compagnie est obligatoire"
            );
        }
    }

    private void verifyCompanyExists(String companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new EntityNotFoundException(
                    "Compagnie introuvable: " + companyId
            );
        }
    }

    private void validateTicketId(String ticketId) {
        if (ticketId == null || ticketId.isBlank()) {
            throw new IllegalArgumentException(
                    "L'identifiant du ticket est obligatoire"
            );
        }

        if (!ticketId.startsWith("ticket-")) {
            throw new IllegalArgumentException(
                    "L'identifiant du ticket doit commencer par ticket-"
            );
        }
    }

    private void validateCreateRequest(CreateTicketRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Les informations du ticket sont obligatoires"
            );
        }

        if (request.getTitre() == null
                || request.getTitre().isBlank()) {
            throw new IllegalArgumentException(
                    "Le titre du ticket est obligatoire"
            );
        }

        if (request.getTitre().trim().length() > 255) {
            throw new IllegalArgumentException(
                    "Le titre ne peut pas dépasser 255 caractères"
            );
        }

        if (request.getDescription() == null
                || request.getDescription().isBlank()) {
            throw new IllegalArgumentException(
                    "La description du ticket est obligatoire"
            );
        }

        if (request.getDescription().trim().length() > 4000) {
            throw new IllegalArgumentException(
                    "La description ne peut pas dépasser 4000 caractères"
            );
        }
    }

    /* =====================================================
       MAPPING
       ===================================================== */

    private TicketDTO toDto(Ticket ticket) {
        TicketDTO dto = new TicketDTO();

        dto.setId(ticket.getId());

        if (ticket.getCompany() != null) {
            dto.setCompanyId(
                    ticket.getCompany().getCompanyId()
            );

            dto.setCompanyName(
                    ticket.getCompany().getName()
            );

            dto.setCompanyEmail(
                    ticket.getCompany().getEmail()
            );
        }

        dto.setTitre(ticket.getTitre());
        dto.setDescription(ticket.getDescription());
        dto.setStatut(ticket.getStatut());
        dto.setReponseAdmin(ticket.getReponseAdmin());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());

        return dto;
    }
}