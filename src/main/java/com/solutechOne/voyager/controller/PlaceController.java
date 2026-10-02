package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.dto.PlaceCreateRequest;
import com.solutechOne.voyager.model.Place;
import com.solutechOne.voyager.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/V1/places")
@Tag(name = "Places", description = "Gestion des places")
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @Operation(summary = "Créer une place")
    @PostMapping
    public ResponseEntity<Place> createPlace(@RequestBody PlaceCreateRequest req) {
        Place created = placeService.createPlace(req);
        return ResponseEntity.status(201).body(created);
    }

    @Operation(summary = "Obtenir toutes les places")
    @GetMapping("/getAll")
    public ResponseEntity<List<Place>> getAllPlaces() {
        return ResponseEntity.ok(placeService.getAllPlaces());
    }

    @Operation(summary = "Obtenir une place par ID")
    @GetMapping("/{id}")
    public ResponseEntity<Place> getPlaceById(@PathVariable("id") String id) {
        return placeService.getPlaceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtenir les places par compagnie")
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Place>> getPlacesByCompanyId(@PathVariable("companyId") String companyId) {
        return ResponseEntity.ok(placeService.getPlacesByCompanyId(companyId));
    }

    @Operation(summary = "Obtenir les places par ville")
    @GetMapping("/city/{cityId}")
    public ResponseEntity<List<Place>> getPlacesByCityId(@PathVariable("cityId") String cityId) {
        return ResponseEntity.ok(placeService.getPlacesByCityId(cityId));
    }

    @Operation(summary = "Supprimer une place")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable("id") String id) {
        placeService.deletePlace(id);
        return ResponseEntity.noContent().build();
    }
}