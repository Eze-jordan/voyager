package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.dto.BasketAmountRequest;
import com.solutechOne.voyager.dto.BasketCreateRequest;
import com.solutechOne.voyager.dto.BasketResponse;
import com.solutechOne.voyager.model.Basket;
import com.solutechOne.voyager.service.BasketService;
import com.solutechOne.voyager.service.InvoiceDownloadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;

import java.util.List;

@RestController
@RequestMapping("/api/V1/baskets")
@Tag(name = "Baskets", description = "Gestion des paniers clients")
public class BasketController {

    private final BasketService service;
    private final InvoiceDownloadService invoiceDownloadService;

    public BasketController(BasketService service, InvoiceDownloadService invoiceDownloadService) {
        this.service = service;
        this.invoiceDownloadService = invoiceDownloadService;
    }

    @Operation(
            summary = "Créer un panier",
            description = "Crée un nouveau panier pour une entreprise avec les informations du client"
    )
    @PostMapping
    public ResponseEntity<BasketResponse> create(@RequestBody BasketCreateRequest req) {
        Basket created = service.create(
                req.companyId,
                req.buyerPhone,
                req.buyerWhatsapp,
                req.buyerEmail
        );
        return ResponseEntity.status(201).body(BasketResponse.fromEntity(created));
    }

    @Operation(
            summary = "Récupérer un panier par ID",
            description = "Retourne les détails d’un panier à partir de son identifiant"
    )
    @GetMapping("/{id}")
    public ResponseEntity<BasketResponse> getById(@PathVariable("id") String basketId) {
        Basket basket = service.getById(basketId);
        return ResponseEntity.ok(BasketResponse.fromEntity(basket));
    }


    @Operation(
            summary = "Récupérer tous les paniers d'une compagnie",
            description = "Retourne tous les paniers associés à une compagnie donnée"
    )
    @GetMapping("/companies/{companyId}/baskets")
    public ResponseEntity<List<BasketResponse>> getByCompany(@PathVariable String companyId) {
        List<BasketResponse> res = service.getByCompany(companyId)
                .stream()
                .map(BasketResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(res);
    }

    @Operation(
            summary = "Mettre à jour le montant du panier",
            description = "Modifie le montant total d’un panier existant"
    )
    @PutMapping("/{id}/amount")
    public ResponseEntity<BasketResponse> updateAmount(
            @PathVariable("id") String basketId,
            @RequestBody BasketAmountRequest req
    ) {
        Basket updated = service.updateAmount(basketId, req.basketAmount);
        return ResponseEntity.ok(BasketResponse.fromEntity(updated));
    }

    @Operation(
            summary = "Abandonner un panier",
            description = "Marque un panier comme abandonné"
    )
    @PutMapping("/{id}/abandon")
    public ResponseEntity<BasketResponse> abandon(@PathVariable("id") String basketId) {
        Basket abandoned = service.abandon(basketId);
        return ResponseEntity.ok(BasketResponse.fromEntity(abandoned));
    }

    @Operation(
            summary = "Supprimer un panier",
            description = "Supprime définitivement un panier à partir de son identifiant"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String basketId) {
        service.delete(basketId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{basketId}/invoice")
    public ResponseEntity<Resource> downloadInvoice(
            @PathVariable String basketId
    ) {

        InvoiceDownloadService.InvoiceDownload download =
                invoiceDownloadService.getByBasketId(basketId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + download.filename()
                                + "\""
                )
                .body(download.resource());
    }
}