package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.model.Itinerary;
import com.solutechOne.voyager.service.ItineraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/V1/itineraries")
public class ItineraryController {

    private final ItineraryService service;

    public ItineraryController(ItineraryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Itinerary> create(@RequestBody Itinerary itinerary) {
        return ResponseEntity.status(201).body(service.create(itinerary));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Itinerary> update(@PathVariable String id, @RequestBody Itinerary itinerary) {
        return ResponseEntity.ok(service.update(id, itinerary));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Itinerary> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<Itinerary>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}