package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.model.TravelClass;
import com.solutechOne.voyager.service.TravelClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/V1/travel-classes")
@Tag(name = "Travel Classes", description = "Gestion des classes de voyage")
public class TravelClassController {

    private final TravelClassService travelClassService;

    public TravelClassController(TravelClassService travelClassService) {
        this.travelClassService = travelClassService;
    }

    @Operation(summary = "Créer une classe de voyage pour une compagnie")
    @PostMapping("/company/{companyId}")
    public ResponseEntity<TravelClass> createTravelClass(
            @PathVariable String companyId,
            @RequestBody TravelClass travelClass) {

        TravelClass createdClass = travelClassService.createTravelClass(companyId, travelClass);
        return ResponseEntity.status(201).body(createdClass);
    }

    @Operation(summary = "Obtenir toutes les classes de voyage")
    @GetMapping("/all")
    public ResponseEntity<List<TravelClass>> getAllTravelClasses() {
        return ResponseEntity.ok(travelClassService.getAllTravelClasses());
    }

    @Operation(summary = "Obtenir une classe de voyage par ID")
    @GetMapping("/{classId}")
    public ResponseEntity<TravelClass> getTravelClassById(@PathVariable String classId) {
        return travelClassService.getTravelClassById(classId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtenir les classes de voyage par compagnie")
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<TravelClass>> getTravelClassesByCompanyId(@PathVariable String companyId) {
        return ResponseEntity.ok(travelClassService.getTravelClassesByCompanyId(companyId));
    }

    @Operation(summary = "Modifier une classe de voyage")
    @PutMapping("/{classId}")
    public ResponseEntity<TravelClass> updateTravelClass(
            @PathVariable String classId,
            @RequestBody TravelClass updatedClass) {
        return ResponseEntity.ok(travelClassService.updateTravelClass(classId, updatedClass));
    }

    @Operation(summary = "Supprimer une classe de voyage")
    @DeleteMapping("/{classId}")
    public ResponseEntity<Void> deleteTravelClass(@PathVariable String classId) {
        travelClassService.deleteTravelClass(classId);
        return ResponseEntity.noContent().build();
    }
}