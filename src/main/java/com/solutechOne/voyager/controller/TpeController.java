package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.model.Tpe;
import com.solutechOne.voyager.service.TpeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/V1/tpe")
@Tag(name = "TPE", description = "Gestion des TPE")
public class TpeController {

    private final TpeService service;

    public TpeController(TpeService service) {
        this.service = service;
    }

    @Operation(summary = "Créer un TPE")
    @PostMapping
    public ResponseEntity<Tpe> create(@RequestBody Tpe tpe) {
        Tpe created = service.create(tpe);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Obtenir un TPE par ID")
    @GetMapping("/{id}")
    public ResponseEntity<Tpe> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Obtenir tous les TPE")
    @GetMapping
    public ResponseEntity<List<Tpe>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @Operation(summary = "Obtenir les TPE par compagnie")
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Tpe>> getByCompany(@PathVariable String companyId) {
        return ResponseEntity.ok(service.getByCompany(companyId));
    }

    @Operation(summary = "Mettre à jour la dernière connexion du TPE")
    @PutMapping("/{id}/connect")
    public ResponseEntity<String> updateLastConnection(@PathVariable String id) {
        service.updateLastConnection(id);
        return ResponseEntity.ok("Dernière connexion mise à jour");
    }

    @Operation(summary = "Supprimer un TPE")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok("TPE supprimé avec succès");
    }
}