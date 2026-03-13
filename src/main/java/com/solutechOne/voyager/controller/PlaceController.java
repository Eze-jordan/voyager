package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.dto.PlaceCreateRequest;
import com.solutechOne.voyager.model.Place;
import com.solutechOne.voyager.service.PlaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/V1/places")
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @PostMapping
    public ResponseEntity<Place> createPlace(@RequestBody PlaceCreateRequest req) {
        Place created = placeService.createPlace(req);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Place>> getAllPlaces() {
        return ResponseEntity.ok(placeService.getAllPlaces());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Place> getPlaceById(@PathVariable("id") String id) {
        return placeService.getPlaceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Place>> getPlacesByCompanyId(@PathVariable("companyId") String companyId) {
        return ResponseEntity.ok(placeService.getPlacesByCompanyId(companyId));
    }

    @GetMapping("/city/{cityId}")
    public ResponseEntity<List<Place>> getPlacesByCityId(@PathVariable("cityId") String cityId) {
        return ResponseEntity.ok(placeService.getPlacesByCityId(cityId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable("id") String id) {
        placeService.deletePlace(id);
        return ResponseEntity.noContent().build();
    }
}