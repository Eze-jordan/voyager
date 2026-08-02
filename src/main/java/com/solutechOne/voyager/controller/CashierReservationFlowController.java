package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.dto.cashier.AddCashierTravelRequest;
import com.solutechOne.voyager.dto.cashier.CreateCashierReservationRequest;
import com.solutechOne.voyager.dto.cashier.OpenCashierBasketRequest;
import com.solutechOne.voyager.dto.cashier.StartCashierPaymentRequest;
import com.solutechOne.voyager.model.Basket;
import com.solutechOne.voyager.model.Reservation;
import com.solutechOne.voyager.model.TicketPrice;
import com.solutechOne.voyager.model.Travel;
import com.solutechOne.voyager.service.CashierReservationFlowService;
import com.solutechOne.voyager.service.TravelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cashiers/{cashierId}")
@Tag(
        name = "Caisse - Parcours de réservation",
        description = """
                Gestion complète du parcours de réservation effectué par un utilisateur caisse :
                ouverture du panier, ajout du voyage, recherche des billets,
                création des réservations, consultation des paniers et paiement.
                """
)
public class CashierReservationFlowController {

    private final CashierReservationFlowService cashierReservationFlowService;
    private final TravelService travelService;

    public CashierReservationFlowController(
            CashierReservationFlowService cashierReservationFlowService,
            TravelService travelService
    ) {
        this.cashierReservationFlowService = cashierReservationFlowService;
        this.travelService = travelService;
    }

    /* =====================================================
       1. OUVRIR UN PANIER
       ===================================================== */

    /**
     * Le caissier ouvre un nouveau panier.
     *
     * POST /api/v1/cashiers/{cashierId}/baskets
     */
    @PostMapping("/baskets")
    @Operation(
            summary = "Ouvrir un panier de réservation",
            description = """
                    Crée un nouveau panier associé à l'utilisateur caisse identifié
                    par cashierId.

                    La compagnie du panier est automatiquement récupérée depuis
                    le compte utilisateur de la caisse.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Panier créé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Basket.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données du panier invalides"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utilisateur caisse introuvable"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Le compte utilisateur ne peut pas ouvrir de panier"
            )
    })
    public ResponseEntity<Basket> openBasket(
            @Parameter(
                    description = "Identifiant de l'utilisateur caisse",
                    required = true,
                    example = "user-17534c82-e8b1-44b1-b02f-a3234380c160"
            )
            @PathVariable String cashierId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Coordonnées du client pour lequel le panier est ouvert",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = OpenCashierBasketRequest.class
                            )
                    )
            )
            @RequestBody OpenCashierBasketRequest request
    ) {
        Basket basket = cashierReservationFlowService.openBasket(
                cashierId,
                request.getBuyerPhone(),
                request.getBuyerWhatsapp(),
                request.getBuyerEmail()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(basket);
    }

    /* =====================================================
       2. LISTE DES PANIERS DU CAISSIER
       ===================================================== */

    /**
     * Retourne tous les paniers d'un caissier.
     *
     * GET /api/v1/cashiers/{cashierId}/baskets
     */
    @GetMapping("/baskets")
    @Operation(
            summary = "Lister les paniers d'un caissier",
            description = """
                    Retourne tous les paniers ouverts ou traités par
                    l'utilisateur caisse identifié par cashierId.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des paniers récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = Basket.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utilisateur caisse introuvable"
            )
    })
    public ResponseEntity<List<Basket>> getCashierBaskets(
            @Parameter(
                    description = "Identifiant de l'utilisateur caisse",
                    required = true,
                    example = "user-17534c82-e8b1-44b1-b02f-a3234380c160"
            )
            @PathVariable String cashierId
    ) {
        List<Basket> baskets =
                cashierReservationFlowService.getBasketsByCashier(cashierId);

        return ResponseEntity.ok(baskets);
    }

    /* =====================================================
       3. CONSULTER UN PANIER DU CAISSIER
       ===================================================== */

    /**
     * Retourne un panier précis appartenant au caissier.
     *
     * GET /api/v1/cashiers/{cashierId}/baskets/{basketId}
     */
    @GetMapping("/baskets/{basketId}")
    @Operation(
            summary = "Consulter un panier du caissier",
            description = """
                    Retourne les informations d'un panier précis après avoir
                    vérifié qu'il appartient bien à l'utilisateur caisse.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Panier récupéré avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Basket.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Caissier ou panier introuvable"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Le panier n'appartient pas à ce caissier"
            )
    })
    public ResponseEntity<Basket> getCashierBasket(
            @Parameter(
                    description = "Identifiant de l'utilisateur caisse",
                    required = true,
                    example = "user-17534c82-e8b1-44b1-b02f-a3234380c160"
            )
            @PathVariable String cashierId,

            @Parameter(
                    description = "Identifiant du panier",
                    required = true,
                    example = "basket-51f67f69"
            )
            @PathVariable String basketId
    ) {
        Basket basket = cashierReservationFlowService.getBasketByCashier(
                cashierId,
                basketId
        );

        return ResponseEntity.ok(basket);
    }

    /* =====================================================
       4. AJOUTER UN VOYAGE AU PANIER
       ===================================================== */

    /**
     * Ajoute un voyage dans le panier du caissier.
     *
     * POST /api/v1/cashiers/{cashierId}/baskets/{basketId}/travels
     */
    @PostMapping("/baskets/{basketId}/travels")
    @Operation(
            summary = "Ajouter un voyage au panier",
            description = """
                    Ajoute un voyage au panier à partir d'un départ et
                    d'une arrivée.

                    Le panier doit appartenir au caissier et être encore
                    dans le statut EN_COURS.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Voyage ajouté au panier avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Travel.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Départ ou arrivée invalide"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Le panier n'appartient pas au caissier"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Caissier, panier, départ ou arrivée introuvable"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Le voyage existe déjà ou le panier n'est plus modifiable"
            )
    })
    public ResponseEntity<Travel> addTravel(
            @Parameter(
                    description = "Identifiant de l'utilisateur caisse",
                    required = true,
                    example = "user-17534c82-e8b1-44b1-b02f-a3234380c160"
            )
            @PathVariable String cashierId,

            @Parameter(
                    description = "Identifiant du panier",
                    required = true,
                    example = "basket-51f67f69"
            )
            @PathVariable String basketId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Départ et arrivée du voyage",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = AddCashierTravelRequest.class
                            )
                    )
            )
            @RequestBody AddCashierTravelRequest request
    ) {
        Travel travel = cashierReservationFlowService.addTravel(
                cashierId,
                basketId,
                request.getDepartureId(),
                request.getArrivalId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(travel);
    }

    /* =====================================================
       5. RECHERCHER LES BILLETS DU VOYAGE
       ===================================================== */

    /**
     * Recherche les billets correspondant au voyage.
     *
     * GET /api/v1/cashiers/{cashierId}/travels/{travelId}/tickets
     */
    @GetMapping("/travels/{travelId}/tickets")
    @Operation(
            summary = "Rechercher les billets d'un voyage",
            description = """
                    Retourne les billets et tarifs disponibles pour le voyage.

                    Le contrôleur vérifie que le voyage appartient à un panier
                    associé au caissier indiqué.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Billets récupérés avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = TicketPrice.class
                                    )
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Le voyage appartient au panier d'un autre caissier"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Caissier, voyage ou panier introuvable"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Le voyage ne possède pas de départ ou d'arrivée valide"
            )
    })
    public ResponseEntity<List<TicketPrice>> getTravelTickets(
            @Parameter(
                    description = "Identifiant de l'utilisateur caisse",
                    required = true,
                    example = "user-17534c82-e8b1-44b1-b02f-a3234380c160"
            )
            @PathVariable String cashierId,

            @Parameter(
                    description = "Identifiant du voyage",
                    required = true,
                    example = "travel-c84310d2"
            )
            @PathVariable String travelId
    ) {
        Travel travel = travelService.getById(travelId);

        if (travel.getBasket() == null) {
            throw new IllegalStateException(
                    "Le voyage n'est associé à aucun panier"
            );
        }

        /*
         * Vérifie que le panier du voyage appartient réellement
         * au caissier transmis dans l'URL.
         */
        cashierReservationFlowService.getBasketByCashier(
                cashierId,
                travel.getBasket().getBasketId()
        );

        List<TicketPrice> tickets =
                travelService.getRelatedTicketsByTravel(travelId);

        return ResponseEntity.ok(tickets);
    }

    /* =====================================================
       6. CRÉER UNE RÉSERVATION
       ===================================================== */

    /**
     * Crée une réservation dans le voyage du caissier.
     *
     * POST /api/v1/cashiers/{cashierId}/travels/{travelId}/reservations
     */
    @PostMapping("/travels/{travelId}/reservations")
    @Operation(
            summary = "Créer une réservation",
            description = """
                    Crée une réservation pour un passager sur un voyage
                    appartenant au panier du caissier.

                    Après la création de la réservation, le siège est attribué
                    automatiquement et le montant du panier est recalculé.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Réservation créée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Reservation.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Informations du passager ou billet invalides"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Le voyage n'appartient pas au caissier"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Caissier, voyage ou billet introuvable"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Le panier ne peut plus recevoir de réservation"
            )
    })
    public ResponseEntity<Reservation> createReservation(
            @Parameter(
                    description = "Identifiant de l'utilisateur caisse",
                    required = true,
                    example = "user-17534c82-e8b1-44b1-b02f-a3234380c160"
            )
            @PathVariable String cashierId,

            @Parameter(
                    description = "Identifiant du voyage",
                    required = true,
                    example = "travel-c84310d2"
            )
            @PathVariable String travelId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Informations du passager et billet sélectionné",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = CreateCashierReservationRequest.class
                            )
                    )
            )
            @RequestBody CreateCashierReservationRequest request
    ) {
        Reservation reservation =
                cashierReservationFlowService.addReservation(
                        cashierId,
                        travelId,
                        request.getPassengerName(),
                        request.getPassengerFirstname(),
                        request.getPassengerDateOfBirth(),
                        request.getPassengerSex(),
                        request.getPassengerNationality(),
                        request.getPassengerMail(),
                        request.getPassengerPhone(),
                        request.getPassengerWhatsapp(),
                        request.getTicketId()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reservation);
    }

    /* =====================================================
       7. DÉMARRER LE PAIEMENT
       ===================================================== */

    /**
     * Démarre le paiement du panier.
     *
     * POST /api/v1/cashiers/{cashierId}/baskets/{basketId}/payments
     */
    @PostMapping("/baskets/{basketId}/payments")
    @Operation(
            summary = "Démarrer le paiement du panier",
            description = """
                    Recalcule le montant du panier, enregistre les informations
                    de paiement et place le panier dans le statut
                    PAYMENT_PENDING.

                    Le panier doit appartenir au caissier et contenir au moins
                    une réservation.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Paiement démarré avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Basket.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Informations de paiement invalides"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Le panier n'appartient pas au caissier"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Caissier ou panier introuvable"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = """
                            Le panier est vide, déjà payé ou possède déjà
                            un paiement en cours
                            """
            )
    })
    public ResponseEntity<Basket> startPayment(
            @Parameter(
                    description = "Identifiant de l'utilisateur caisse",
                    required = true,
                    example = "user-17534c82-e8b1-44b1-b02f-a3234380c160"
            )
            @PathVariable String cashierId,

            @Parameter(
                    description = "Identifiant du panier",
                    required = true,
                    example = "basket-51f67f69"
            )
            @PathVariable String basketId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Informations de la transaction de paiement",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = StartCashierPaymentRequest.class
                            )
                    )
            )
            @RequestBody StartCashierPaymentRequest request
    ) {
        Basket basket = cashierReservationFlowService.startPayment(
                cashierId,
                basketId,
                request.getPaymentService(),
                request.getPaymentId(),
                request.getPaymentAccount()
        );

        return ResponseEntity.ok(basket);
    }
}