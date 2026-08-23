package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.dto.DepartureCreateRequest;
import com.solutechOne.voyager.model.Departure;
import com.solutechOne.voyager.service.DepartureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/V1/departures")
@Tag(name = "Departures", description = "Gestion des départs")
public class DepartureController {

    private final DepartureService departureService;

    public DepartureController(DepartureService departureService) {
        this.departureService = departureService;
    }

    @Operation(summary = "Créer un départ")
    @PostMapping
    public ResponseEntity<Departure> createDeparture(@RequestBody DepartureCreateRequest req) {
        Departure createdDeparture = departureService.createDeparture(req);
        return ResponseEntity.status(201).body(createdDeparture);
    }

    @Operation(summary = "Récupérer tous les départs")
    @GetMapping("/getAll")
    public ResponseEntity<List<Departure>> getAllDepartures() {
        return ResponseEntity.ok(departureService.getAllDepartures());
    }

    @Operation(summary = "Récupérer un départ par ID")
    @GetMapping("/{id}")
    public ResponseEntity<Departure> getDepartureById(@PathVariable String id) {
        return departureService.getDepartureById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Récupérer les départs d’une compagnie")
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Departure>> getByCompany(@PathVariable String companyId) {
        return ResponseEntity.ok(departureService.getDeparturesByCompany(companyId));
    }

    @Operation(summary = "Modifier un départ")
    @PutMapping("/{id}")
    public ResponseEntity<Departure> updateDeparture(@PathVariable String id, @RequestBody Departure departure) {
        Departure updated = departureService.updateDeparture(id, departure);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Supprimer un départ")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeparture(@PathVariable String id) {
        departureService.deleteDeparture(id);
        return ResponseEntity.noContent().build();
    }
}