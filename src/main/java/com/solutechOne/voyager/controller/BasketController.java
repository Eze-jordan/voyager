package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.dto.BasketAmountRequest;
import com.solutechOne.voyager.dto.BasketPayRequest;
import com.solutechOne.voyager.dto.BasketResponse;
import com.solutechOne.voyager.model.Basket;
import com.solutechOne.voyager.service.BasketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/V1/baskets")
public class BasketController {

    private final BasketService service;

    public BasketController(BasketService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BasketResponse> create(@RequestParam String companyId) {
        Basket created = service.create(companyId);
        return ResponseEntity.status(201).body(BasketResponse.fromEntity(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BasketResponse> getById(@PathVariable("id") String basketId) {
        Basket b = service.getById(basketId);
        return ResponseEntity.ok(BasketResponse.fromEntity(b));
    }

    @GetMapping
    public ResponseEntity<List<BasketResponse>> getByCompany(@RequestParam String companyId) {
        List<BasketResponse> res = service.getByCompany(companyId)
                .stream()
                .map(BasketResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(res);
    }

    @PutMapping("/{id}/amount")
    public ResponseEntity<BasketResponse> updateAmount(
            @PathVariable("id") String basketId,
            @RequestBody BasketAmountRequest req
    ) {
        Basket updated = service.updateAmount(basketId, req.basketAmount);
        return ResponseEntity.ok(BasketResponse.fromEntity(updated));
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<BasketResponse> pay(
            @PathVariable("id") String basketId,
            @RequestBody BasketPayRequest req
    ) {
        Basket paid = service.pay(
                basketId,
                req.basketPaymentService,
                req.basketPaymentId,
                req.basketPaymentAccount
        );
        return ResponseEntity.ok(BasketResponse.fromEntity(paid));
    }

    @PutMapping("/{id}/abandon")
    public ResponseEntity<BasketResponse> abandon(@PathVariable("id") String basketId) {
        Basket abandoned = service.abandon(basketId);
        return ResponseEntity.ok(BasketResponse.fromEntity(abandoned));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String basketId) {
        service.delete(basketId);
        return ResponseEntity.noContent().build();
    }
}