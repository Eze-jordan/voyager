package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.model.TpeAcl;
import com.solutechOne.voyager.service.TpeAclService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/V1/tpe-acl")
@Tag(name = "TPE ACL", description = "Gestion des accès TPE")
public class TpeAclController {

    private final TpeAclService service;

    public TpeAclController(TpeAclService service) {
        this.service = service;
    }

    @Operation(summary = "Créer une ACL TPE")
    @PostMapping
    public ResponseEntity<TpeAcl> create(@RequestBody TpeAcl acl) {
        return ResponseEntity.status(201).body(service.create(acl));
    }

    @Operation(summary = "Obtenir une ACL TPE par ID")
    @GetMapping("/{id}")
    public ResponseEntity<TpeAcl> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Obtenir toutes les ACL TPE")
    @GetMapping
    public ResponseEntity<List<TpeAcl>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @Operation(summary = "Obtenir les ACL TPE par compagnie")
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<TpeAcl>> getByCompany(@PathVariable String companyId) {
        return ResponseEntity.ok(service.getByCompany(companyId));
    }

    @Operation(summary = "Obtenir les ACL par TPE")
    @GetMapping("/tpe/{tpeId}")
    public ResponseEntity<List<TpeAcl>> getByTpe(@PathVariable String tpeId) {
        return ResponseEntity.ok(service.getByTpe(tpeId));
    }

    @Operation(summary = "Obtenir les ACL par place")
    @GetMapping("/place/{placeId}")
    public ResponseEntity<List<TpeAcl>> getByPlace(@PathVariable String placeId) {
        return ResponseEntity.ok(service.getByPlace(placeId));
    }

    @Operation(summary = "Supprimer une ACL TPE")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok("ACL supprimé");
    }
}