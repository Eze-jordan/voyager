package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.dto.TravelCreateRequest;
import com.solutechOne.voyager.dto.TravelResponse;
import com.solutechOne.voyager.model.Travel;
import com.solutechOne.voyager.service.TravelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/V1/travels")
public class TravelController {

    private final TravelService service;

    public TravelController(TravelService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TravelResponse> create(@RequestBody TravelCreateRequest req) {
        Travel created = service.create(
                req.basketId,
                req.departureId,
                req.arrivalId,
                req.ticketPriceId
        );
        return ResponseEntity.status(201).body(TravelResponse.fromEntity(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelResponse> getById(@PathVariable("id") String travelId) {
        return ResponseEntity.ok(TravelResponse.fromEntity(service.getById(travelId)));
    }

    @GetMapping
    public ResponseEntity<List<TravelResponse>> getByBasket(@RequestParam String basketId) {
        List<TravelResponse> res = service.getByBasket(basketId)
                .stream()
                .map(TravelResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String travelId) {
        service.delete(travelId);
        return ResponseEntity.noContent().build();
    }
}