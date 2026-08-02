package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.dto.CreateTicketRequest;
import com.solutechOne.voyager.dto.TicketDTO;
import com.solutechOne.voyager.dto.UpdateTicketStatusRequest;
import com.solutechOne.voyager.enums.StatutTicket;
import com.solutechOne.voyager.service.TicketService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/V1/ticket")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /* =====================================================
       ROUTES COMPAGNIE
       ===================================================== */

    /**
     * Une compagnie crée un ticket destiné aux administrateurs.
     *
     * POST /api/v1/companies/{companyId}/tickets
     */
    @Tag(
            name = "Tickets - Compagnies",
            description = "Création et consultation des tickets par les compagnies"
    )
    @Operation(
            operationId = "createCompanyTicket",
            summary = "Créer un ticket",
            description = """
                    Crée un nouveau ticket pour une compagnie.

                    Le ticket est automatiquement relié à la compagnie indiquée
                    par companyId et son statut initial est OUVERT.

                    L'identifiant généré commence par ticket-.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Ticket créé avec succès",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TicketDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données du ticket invalides",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Compagnie introuvable",
                    content = @Content
            )
    })
    @PostMapping(
            value = "/companies/{companyId}/tickets",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TicketDTO> createTicket(
            @Parameter(
                    description = "Identifiant de la compagnie créant le ticket",
                    required = true,
                    example = "500001"
            )
            @PathVariable String companyId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Titre et description du ticket",
                    content = @Content(
                            schema = @Schema(
                                    implementation = CreateTicketRequest.class
                            )
                    )
            )
            @RequestBody CreateTicketRequest request
    ) {
        TicketDTO createdTicket = ticketService.createTicket(
                companyId,
                request
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdTicket);
    }

    /**
     * Recherche tous les tickets d'une compagnie depuis son ID.
     *
     * GET /api/v1/companies/{companyId}/tickets
     */
    @Tag(
            name = "Tickets - Compagnies",
            description = "Création et consultation des tickets par les compagnies"
    )
    @Operation(
            operationId = "findCompanyTickets",
            summary = "Lister les tickets d'une compagnie",
            description = """
                    Retourne tous les tickets appartenant à la compagnie indiquée.

                    Les tickets sont retournés du plus récent au plus ancien.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des tickets de la compagnie",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = TicketDTO.class
                                    )
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identifiant de compagnie invalide",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Compagnie introuvable",
                    content = @Content
            )
    })
    @GetMapping(
            value = "/companies/{companyId}/tickets",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<TicketDTO>> findCompanyTickets(
            @Parameter(
                    description = "Identifiant de la compagnie recherchée",
                    required = true,
                    example = "500001"
            )
            @PathVariable String companyId
    ) {
        return ResponseEntity.ok(
                ticketService.findByCompany(companyId)
        );
    }

    /**
     * Recherche un ticket précis appartenant à une compagnie.
     *
     * GET /api/v1/companies/{companyId}/tickets/{ticketId}
     */
    @Tag(
            name = "Tickets - Compagnies",
            description = "Création et consultation des tickets par les compagnies"
    )
    @Operation(
            operationId = "findCompanyTicketById",
            summary = "Consulter un ticket d'une compagnie",
            description = """
                    Recherche un ticket précis en vérifiant qu'il appartient
                    réellement à la compagnie indiquée.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ticket trouvé",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TicketDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identifiant invalide",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ticket introuvable pour cette compagnie",
                    content = @Content
            )
    })
    @GetMapping(
            value = "/companies/{companyId}/tickets/{ticketId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TicketDTO> findCompanyTicketById(
            @Parameter(
                    description = "Identifiant de la compagnie",
                    required = true,
                    example = "500001"
            )
            @PathVariable String companyId,

            @Parameter(
                    description = "Identifiant du ticket commençant par ticket-",
                    required = true,
                    example = "ticket-6f86c75e-c1a4-4e4d-94f3-dc06156ef13e"
            )
            @PathVariable String ticketId
    ) {
        return ResponseEntity.ok(
                ticketService.findByCompanyAndTicketId(
                        companyId,
                        ticketId
                )
        );
    }

    /**
     * Recherche les tickets d'une compagnie selon leur statut.
     *
     * GET /api/v1/companies/{companyId}/tickets/status/OUVERT
     */
    @Tag(
            name = "Tickets - Compagnies",
            description = "Création et consultation des tickets par les compagnies"
    )
    @Operation(
            operationId = "findCompanyTicketsByStatus",
            summary = "Filtrer les tickets d'une compagnie par statut",
            description = """
                    Retourne les tickets d'une compagnie correspondant
                    au statut demandé.

                    Statuts acceptés :
                    OUVERT, EN_COURS, RESOLU et FERME.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des tickets correspondant au statut",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = TicketDTO.class
                                    )
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Statut ou identifiant invalide",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Compagnie introuvable",
                    content = @Content
            )
    })
    @GetMapping(
            value = "/companies/{companyId}/tickets/status/{statut}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<TicketDTO>> findCompanyTicketsByStatut(
            @Parameter(
                    description = "Identifiant de la compagnie",
                    required = true,
                    example = "500001"
            )
            @PathVariable String companyId,

            @Parameter(
                    description = "Statut des tickets recherchés",
                    required = true,
                    example = "OUVERT",
                    schema = @Schema(
                            allowableValues = {
                                    "OUVERT",
                                    "EN_COURS",
                                    "RESOLU",
                                    "FERME"
                            }
                    )
            )
            @PathVariable StatutTicket statut
    ) {
        return ResponseEntity.ok(
                ticketService.findByCompanyAndStatut(
                        companyId,
                        statut
                )
        );
    }

    /* =====================================================
       ROUTES ADMINISTRATEUR
       ===================================================== */

    /**
     * Retourne tous les tickets.
     *
     * GET /api/v1/admin/tickets
     */
    @Tag(
            name = "Tickets - Administration",
            description = "Consultation et traitement des tickets par les administrateurs"
    )
    @Operation(
            operationId = "findAllTickets",
            summary = "Lister tous les tickets",
            description = """
                    Retourne tous les tickets créés par toutes les compagnies.

                    Cette route est destinée aux administrateurs.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste complète des tickets",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = TicketDTO.class
                                    )
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentification requise",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accès administrateur refusé",
                    content = @Content
            )
    })
    @GetMapping(
            value = "/admin/tickets",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<TicketDTO>> findAllTickets() {
        return ResponseEntity.ok(
                ticketService.findAll()
        );
    }

    /**
     * Recherche un ticket depuis son identifiant.
     *
     * GET /api/v1/admin/tickets/ticket-xxxx
     */
    @Tag(
            name = "Tickets - Administration",
            description = "Consultation et traitement des tickets par les administrateurs"
    )
    @Operation(
            operationId = "findTicketById",
            summary = "Consulter un ticket",
            description = """
                    Recherche un ticket depuis son identifiant unique.

                    L'identifiant doit commencer par ticket-.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ticket trouvé",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TicketDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identifiant du ticket invalide",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ticket introuvable",
                    content = @Content
            )
    })
    @GetMapping(
            value = "/admin/tickets/{ticketId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TicketDTO> findTicketById(
            @Parameter(
                    description = "Identifiant du ticket",
                    required = true,
                    example = "ticket-6f86c75e-c1a4-4e4d-94f3-dc06156ef13e"
            )
            @PathVariable String ticketId
    ) {
        return ResponseEntity.ok(
                ticketService.findById(ticketId)
        );
    }

    /**
     * Recherche tous les tickets selon leur statut.
     *
     * GET /api/v1/admin/tickets/status/OUVERT
     */
    @Tag(
            name = "Tickets - Administration",
            description = "Consultation et traitement des tickets par les administrateurs"
    )
    @Operation(
            operationId = "findTicketsByStatus",
            summary = "Filtrer tous les tickets par statut",
            description = """
                    Retourne tous les tickets correspondant au statut demandé,
                    toutes compagnies confondues.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des tickets correspondant au statut",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = TicketDTO.class
                                    )
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Statut invalide",
                    content = @Content
            )
    })
    @GetMapping(
            value = "/admin/tickets/status/{statut}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<TicketDTO>> findTicketsByStatut(
            @Parameter(
                    description = "Statut des tickets recherchés",
                    required = true,
                    example = "OUVERT",
                    schema = @Schema(
                            allowableValues = {
                                    "OUVERT",
                                    "EN_COURS",
                                    "RESOLU",
                                    "FERME"
                            }
                    )
            )
            @PathVariable StatutTicket statut
    ) {
        return ResponseEntity.ok(
                ticketService.findByStatut(statut)
        );
    }

    /**
     * Réponse et modification du statut par un administrateur.
     *
     * PATCH /api/v1/admin/tickets/{ticketId}
     */
    @Tag(
            name = "Tickets - Administration",
            description = "Consultation et traitement des tickets par les administrateurs"
    )
    @Operation(
            operationId = "updateTicketByAdmin",
            summary = "Répondre à un ticket",
            description = """
                    Permet à un administrateur d'ajouter une réponse
                    et de modifier le statut du ticket.

                    Exemple :
                    {
                      "statut": "RESOLU",
                      "reponseAdmin": "Le problème a été corrigé."
                    }
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ticket mis à jour avec succès",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TicketDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données de mise à jour invalides",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentification requise",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accès administrateur refusé",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ticket introuvable",
                    content = @Content
            )
    })
    @PatchMapping(
            value = "/admin/tickets/{ticketId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TicketDTO> updateTicketByAdmin(
            @Parameter(
                    description = "Identifiant du ticket à traiter",
                    required = true,
                    example = "ticket-6f86c75e-c1a4-4e4d-94f3-dc06156ef13e"
            )
            @PathVariable String ticketId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Nouveau statut et réponse de l'administrateur",
                    content = @Content(
                            schema = @Schema(
                                    implementation = UpdateTicketStatusRequest.class
                            )
                    )
            )
            @RequestBody UpdateTicketStatusRequest request
    ) {
        return ResponseEntity.ok(
                ticketService.updateStatut(
                        ticketId,
                        request
                )
        );
    }

    /**
     * Suppression d'un ticket par un administrateur.
     *
     * DELETE /api/v1/admin/tickets/{ticketId}
     */
    @Tag(
            name = "Tickets - Administration",
            description = "Consultation et traitement des tickets par les administrateurs"
    )
    @Operation(
            operationId = "deleteTicket",
            summary = "Supprimer définitivement un ticket",
            description = """
                    Supprime définitivement un ticket.

                    Cette opération doit être réservée aux administrateurs.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ticket supprimé avec succès",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Map.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentification requise",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accès administrateur refusé",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ticket introuvable",
                    content = @Content
            )
    })
    @DeleteMapping(
            value = "/admin/tickets/{ticketId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, String>> deleteTicket(
            @Parameter(
                    description = "Identifiant du ticket à supprimer",
                    required = true,
                    example = "ticket-6f86c75e-c1a4-4e4d-94f3-dc06156ef13e"
            )
            @PathVariable String ticketId
    ) {
        ticketService.delete(ticketId);

        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "message", "Le ticket a été supprimé avec succès",
                        "ticketId", ticketId
                )
        );
    }
}