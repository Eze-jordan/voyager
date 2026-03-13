package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.model.TravelArrival;
import com.solutechOne.voyager.service.TravelArrivalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/V1/arrivals")
public class TravelArrivalController {

    private final TravelArrivalService service;

    public TravelArrivalController(TravelArrivalService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TravelArrival> create(@RequestBody TravelArrival arrival) {
        return ResponseEntity.status(201).body(service.create(arrival));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelArrival> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<TravelArrival>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/departure/{departureId}")
    public ResponseEntity<List<TravelArrival>> getByDeparture(@PathVariable String departureId) {
        return ResponseEntity.ok(service.getByDeparture(departureId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TravelArrival> update(@PathVariable String id,
                                                @RequestBody TravelArrival arrival) {
        return ResponseEntity.ok(service.update(id, arrival));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}