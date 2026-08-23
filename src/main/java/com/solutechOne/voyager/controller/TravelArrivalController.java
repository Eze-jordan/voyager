package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.model.TravelArrival;
import com.solutechOne.voyager.service.TravelArrivalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/V1/arrivals")
@Tag(name = "Travel Arrivals", description = "Gestion des arrivées de voyage")
public class TravelArrivalController {

    private final TravelArrivalService service;

    public TravelArrivalController(TravelArrivalService service) {
        this.service = service;
    }

    @Operation(summary = "Créer une arrivée")
    @PostMapping
    public ResponseEntity<TravelArrival> create(@RequestBody TravelArrival arrival) {
        return ResponseEntity.status(201).body(service.create(arrival));
    }

    @Operation(summary = "Obtenir une arrivée par ID")
    @GetMapping("/{id}")
    public ResponseEntity<TravelArrival> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Obtenir toutes les arrivées")
    @GetMapping("/getAll")
    public ResponseEntity<List<TravelArrival>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @Operation(summary = "Obtenir les arrivées par départ")
    @GetMapping("/departure/{departureId}")
    public ResponseEntity<List<TravelArrival>> getByDeparture(@PathVariable String departureId) {
        return ResponseEntity.ok(service.getByDeparture(departureId));
    }

    @Operation(summary = "Modifier une arrivée")
    @PutMapping("/{id}")
    public ResponseEntity<TravelArrival> update(@PathVariable String id,
                                                @RequestBody TravelArrival arrival) {
        return ResponseEntity.ok(service.update(id, arrival));
    }

    @Operation(summary = "Supprimer une arrivée")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}