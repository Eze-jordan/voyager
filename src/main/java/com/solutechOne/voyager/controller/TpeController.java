package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.model.Tpe;
import com.solutechOne.voyager.service.TpeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/V1/tpe")
public class TpeController {

    private final TpeService service;

    public TpeController(TpeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Tpe> create(@RequestBody Tpe tpe) {
        Tpe created = service.create(tpe);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tpe> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<Tpe>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Tpe>> getByCompany(@PathVariable String companyId) {
        return ResponseEntity.ok(service.getByCompany(companyId));
    }

    @PutMapping("/{id}/connect")
    public ResponseEntity<String> updateLastConnection(@PathVariable String id) {
        service.updateLastConnection(id);
        return ResponseEntity.ok("Dernière connexion mise à jour");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok("TPE supprimé avec succès");
    }
}