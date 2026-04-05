package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.model.TpeAcl;
import com.solutechOne.voyager.service.TpeAclService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/V1/tpe-acl")
public class TpeAclController {

    private final TpeAclService service;

    public TpeAclController(TpeAclService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TpeAcl> create(@RequestBody TpeAcl acl) {
        return ResponseEntity.status(201).body(service.create(acl));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TpeAcl> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<TpeAcl>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<TpeAcl>> getByCompany(@PathVariable String companyId) {
        return ResponseEntity.ok(service.getByCompany(companyId));
    }

    @GetMapping("/tpe/{tpeId}")
    public ResponseEntity<List<TpeAcl>> getByTpe(@PathVariable String tpeId) {
        return ResponseEntity.ok(service.getByTpe(tpeId));
    }

    @GetMapping("/place/{placeId}")
    public ResponseEntity<List<TpeAcl>> getByPlace(@PathVariable String placeId) {
        return ResponseEntity.ok(service.getByPlace(placeId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok("ACL supprimé");
    }
}