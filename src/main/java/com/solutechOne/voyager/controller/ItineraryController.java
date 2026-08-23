package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.model.Itinerary;
import com.solutechOne.voyager.service.ItineraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/V1/itineraries")
@Tag(name = "Itineraries", description = "Gestion des itinéraires")
public class ItineraryController {

    private final ItineraryService service;

    public ItineraryController(ItineraryService service) {
        this.service = service;
    }

    @Operation(summary = "Créer un itinéraire")
    @PostMapping
    public ResponseEntity<Itinerary> create(@RequestBody Itinerary itinerary) {
        return ResponseEntity.status(201).body(service.create(itinerary));
    }

    @Operation(summary = "Modifier un itinéraire")
    @PutMapping("/{id}")
    public ResponseEntity<Itinerary> update(@PathVariable String id, @RequestBody Itinerary itinerary) {
        return ResponseEntity.ok(service.update(id, itinerary));
    }

    @Operation(summary = "Récupérer un itinéraire par ID")
    @GetMapping("/{id}")
    public ResponseEntity<Itinerary> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Récupérer tous les itinéraires")
    @GetMapping("/all")
    public ResponseEntity<List<Itinerary>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @Operation(summary = "Supprimer un itinéraire")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}