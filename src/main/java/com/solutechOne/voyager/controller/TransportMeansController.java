package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.model.TransportMeans;
import com.solutechOne.voyager.service.TransportMeansService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/V1/transport-means")
public class TransportMeansController {

    private final TransportMeansService service;

    public TransportMeansController(TransportMeansService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TransportMeans> create(@RequestParam String companyId,
                                                 @RequestBody TransportMeans body) {
        TransportMeans created = service.create(companyId, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<TransportMeans>> list(@RequestParam String companyId) {
        return ResponseEntity.ok(service.listByCompany(companyId));
    }

    @GetMapping("/{meansId}")
    public ResponseEntity<TransportMeans> get(@PathVariable String meansId,
                                              @RequestParam String companyId) {
        return ResponseEntity.ok(service.getByIdAndCompany(meansId, companyId));
    }

    @PatchMapping("/{meansId}")
    public ResponseEntity<TransportMeans> update(@PathVariable String meansId,
                                                 @RequestParam String companyId,
                                                 @RequestBody TransportMeans body) {
        return ResponseEntity.ok(service.update(meansId, companyId, body));
    }

    @DeleteMapping("/{meansId}")
    public ResponseEntity<Void> delete(@PathVariable String meansId,
                                       @RequestParam String companyId) {
        service.delete(meansId, companyId);
        return ResponseEntity.noContent().build();
    }
}