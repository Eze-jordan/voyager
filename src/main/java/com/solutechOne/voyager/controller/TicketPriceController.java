package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.model.TicketPrice;
import com.solutechOne.voyager.service.TicketPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/V1/ticket-prices")
@Tag(name = "Ticket Prices", description = "Gestion des prix des tickets")
public class TicketPriceController {

    private final TicketPriceService service;

    public TicketPriceController(TicketPriceService service) {
        this.service = service;
    }

    // =========================
    // CREATE
    // =========================
    @Operation(summary = "Créer un prix de ticket")
    @PostMapping
    public ResponseEntity<TicketPrice> create(@RequestBody TicketPrice ticketPrice) {
        TicketPrice created = service.create(ticketPrice);
        return ResponseEntity.status(201).body(created);
    }

    // =========================
    // GET BY ID
    // =========================
    @Operation(summary = "Obtenir un prix de ticket par ID")
    @GetMapping("/{id}")
    public ResponseEntity<TicketPrice> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // =========================
    // GET ALL
    // =========================
    @Operation(summary = "Obtenir tous les prix de tickets")
    @GetMapping
    public ResponseEntity<List<TicketPrice>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // =========================
    // GET BY COMPANY
    // =========================
    @Operation(summary = "Obtenir les prix de tickets par compagnie")
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<TicketPrice>> getByCompany(@PathVariable String companyId) {
        return ResponseEntity.ok(service.getByCompany(companyId));
    }

    // =========================
    // GET BY ROUTE
    // =========================
    @Operation(summary = "Obtenir les prix de tickets par trajet")
    @GetMapping("/route")
    public ResponseEntity<List<TicketPrice>> getByRoute(
            @RequestParam String departureCityId,
            @RequestParam String arrivalCityId) {

        return ResponseEntity.ok(service.getByRoute(departureCityId, arrivalCityId));
    }

    // =========================
    // UPDATE
    // =========================
    @Operation(summary = "Modifier un prix de ticket")
    @PutMapping("/{id}")
    public ResponseEntity<TicketPrice> update(
            @PathVariable String id,
            @RequestBody TicketPrice ticketPrice) {

        TicketPrice updated = service.update(id, ticketPrice);
        return ResponseEntity.ok(updated);
    }

    // =========================
    // DELETE
    // =========================
    @Operation(summary = "Supprimer un prix de ticket")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // =========================
    // CALCUL FINAL PRICE
    // =========================
    @Operation(summary = "Calculer le prix final d’un ticket")
    @GetMapping("/{id}/final-price")
    public ResponseEntity<BigDecimal> calculateFinalPrice(@PathVariable String id) {
        return ResponseEntity.ok(service.calculateFinalPrice(id));
    }
}