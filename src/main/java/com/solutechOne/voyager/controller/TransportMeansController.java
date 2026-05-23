package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.model.TransportMeans;
import com.solutechOne.voyager.service.TransportMeansService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/V1/transport-means")
@Tag(name = "Transport Means", description = "Gestion des moyens de transport")
public class TransportMeansController {

    private final TransportMeansService service;

    public TransportMeansController(TransportMeansService service) {
        this.service = service;
    }

    @Operation(summary = "Créer un moyen de transport")
    @PostMapping
    public ResponseEntity<TransportMeans> create(@RequestParam String companyId,
                                                 @RequestBody TransportMeans body) {
        TransportMeans created = service.create(companyId, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Lister les moyens de transport par compagnie")
    @GetMapping
    public ResponseEntity<List<TransportMeans>> list(@RequestParam String companyId) {
        return ResponseEntity.ok(service.listByCompany(companyId));
    }

    @Operation(summary = "Obtenir un moyen de transport par ID et compagnie")
    @GetMapping("/{meansId}")
    public ResponseEntity<TransportMeans> get(@PathVariable String meansId,
                                              @RequestParam String companyId) {
        return ResponseEntity.ok(service.getByIdAndCompany(meansId, companyId));
    }

    @Operation(summary = "Modifier un moyen de transport")
    @PatchMapping("/{meansId}")
    public ResponseEntity<TransportMeans> update(@PathVariable String meansId,
                                                 @RequestParam String companyId,
                                                 @RequestBody TransportMeans body) {
        return ResponseEntity.ok(service.update(meansId, companyId, body));
    }

    @Operation(summary = "Supprimer un moyen de transport")
    @DeleteMapping("/{meansId}")
    public ResponseEntity<Void> delete(@PathVariable String meansId,
                                       @RequestParam String companyId) {
        service.delete(meansId, companyId);
        return ResponseEntity.noContent().build();
    }
}