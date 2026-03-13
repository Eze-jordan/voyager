package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.model.TravelClass;
import com.solutechOne.voyager.service.TravelClassService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/V1/travel-classes")
public class TravelClassController {

    private final TravelClassService travelClassService;

    public TravelClassController(TravelClassService travelClassService) {
        this.travelClassService = travelClassService;
    }

    @PostMapping("/company/{companyId}")
    public ResponseEntity<TravelClass> createTravelClass(
            @PathVariable String companyId,
            @RequestBody TravelClass travelClass) {

        TravelClass createdClass = travelClassService.createTravelClass(companyId, travelClass);
        return ResponseEntity.status(201).body(createdClass);
    }

    @GetMapping
    public ResponseEntity<List<TravelClass>> getAllTravelClasses() {
        return ResponseEntity.ok(travelClassService.getAllTravelClasses());
    }

    @GetMapping("/{classId}")
    public ResponseEntity<TravelClass> getTravelClassById(@PathVariable String classId) {
        return travelClassService.getTravelClassById(classId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<TravelClass>> getTravelClassesByCompanyId(@PathVariable String companyId) {
        return ResponseEntity.ok(travelClassService.getTravelClassesByCompanyId(companyId));
    }

    @PutMapping("/{classId}")
    public ResponseEntity<TravelClass> updateTravelClass(
            @PathVariable String classId,
            @RequestBody TravelClass updatedClass) {
        return ResponseEntity.ok(travelClassService.updateTravelClass(classId, updatedClass));
    }

    @DeleteMapping("/{classId}")
    public ResponseEntity<Void> deleteTravelClass(@PathVariable String classId) {
        travelClassService.deleteTravelClass(classId);
        return ResponseEntity.noContent().build();
    }
}