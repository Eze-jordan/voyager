package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.dto.CompanyDTO;
import com.solutechOne.voyager.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/V1/companies")
@Tag(name = "Companies", description = "Endpoints for managing transport companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')") // adapte à ton enum Role
    @Operation(summary = "Create a company account")
    public ResponseEntity<CompanyDTO> create(@Valid @RequestBody CompanyDTO body) {
        CompanyDTO created = companyService.create(body);
        return ResponseEntity
                .created(URI.create("/api/V1/companies/" + created.getId()))
                .body(created);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Get all companies")
    public ResponseEntity<List<CompanyDTO>> findAll() {
        return ResponseEntity.ok(companyService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Get company by ID")
    public ResponseEntity<CompanyDTO> findById(@PathVariable String id) {
        return ResponseEntity.ok(companyService.findById(id));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update company (partial)")
    public ResponseEntity<CompanyDTO> patch(@PathVariable String id, @RequestBody CompanyDTO body) {
        return ResponseEntity.ok(companyService.patch(id, body));
    }

    @PutMapping("/{id}/suspend")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Suspend a company")
    public ResponseEntity<Map<String, String>> suspend(@PathVariable String id) {
        companyService.suspend(id);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Compagnie suspendue avec succès"
        ));
    }

    @PutMapping("/{id}/reactivate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Reactivate a company")
    public ResponseEntity<Map<String, String>> reactivate(@PathVariable String id) {
        companyService.reactivate(id);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Compagnie réactivée avec succès"
        ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Delete company by ID")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        companyService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}/archive")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    @Operation(summary = "Archiver une compagnie (soft delete)")
    public ResponseEntity<Map<String, String>> archive(@PathVariable String id) {
        return ResponseEntity.ok(companyService.archive(id));
    }

    @PutMapping("/{id}/unarchive")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    @Operation(summary = "Désarchiver une compagnie")
    public ResponseEntity<Map<String, String>> unarchive(@PathVariable String id) {
        return ResponseEntity.ok(companyService.unarchive(id));
    }
    @PostMapping("/{id}/logo")
    public ResponseEntity<CompanyDTO> uploadLogo(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(companyService.uploadLogo(id, file));
    }
}
