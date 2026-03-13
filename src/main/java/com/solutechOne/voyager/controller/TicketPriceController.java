package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.model.TicketPrice;
import com.solutechOne.voyager.service.TicketPriceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/V1/ticket-prices")
public class TicketPriceController {

    private final TicketPriceService service;

    public TicketPriceController(TicketPriceService service) {
        this.service = service;
    }

    // =========================
    // CREATE
    // =========================
    @PostMapping
    public ResponseEntity<TicketPrice> create(@RequestBody TicketPrice ticketPrice) {
        TicketPrice created = service.create(ticketPrice);
        return ResponseEntity.status(201).body(created);
    }

    // =========================
    // GET BY ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<TicketPrice> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // =========================
    // GET ALL
    // =========================
    @GetMapping
    public ResponseEntity<List<TicketPrice>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // =========================
    // GET BY COMPANY
    // =========================
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<TicketPrice>> getByCompany(@PathVariable String companyId) {
        return ResponseEntity.ok(service.getByCompany(companyId));
    }

    // =========================
    // GET BY ROUTE
    // =========================
    @GetMapping("/route")
    public ResponseEntity<List<TicketPrice>> getByRoute(
            @RequestParam String departureCityId,
            @RequestParam String arrivalCityId) {

        return ResponseEntity.ok(service.getByRoute(departureCityId, arrivalCityId));
    }

    // =========================
    // UPDATE
    // =========================
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // =========================
    // CALCUL FINAL PRICE
    // =========================
    @GetMapping("/{id}/final-price")
    public ResponseEntity<BigDecimal> calculateFinalPrice(@PathVariable String id) {
        return ResponseEntity.ok(service.calculateFinalPrice(id));
    }
}