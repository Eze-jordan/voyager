package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.model.AuditLog;
import com.solutechOne.voyager.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/V1/audit-logs")
@Tag(name = "AuditLogs", description = "Journal des actions (audit)")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    /** 📜 Récupérer tous les logs */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @Operation(summary = "Lister tous les logs")
    public ResponseEntity<List<AuditLog>> getAll() {
        return ResponseEntity.ok(auditLogService.getAll());
    }

    /** 🔍 Rechercher par utilisateur */
    @PostMapping("/by-user") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @Operation(summary = "Filtrer les logs par utilisateur (email)")
    public ResponseEntity<List<AuditLog>> getByUser(@RequestBody Map<String, String> body)
    { String userEmail = body.get("userEmail");
        return ResponseEntity.ok(auditLogService.getByUser(userEmail)); }



    /** 📅 Filtrer par intervalle de date */
    @GetMapping("/by-date")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @Operation(summary = "Filtrer les logs par période")
    public ResponseEntity<List<AuditLog>> getByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(auditLogService.getByDate(start, end));
    }
}
