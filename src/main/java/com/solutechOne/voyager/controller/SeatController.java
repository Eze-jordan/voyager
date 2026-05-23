package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.dto.GenerateSeatRequest;
import com.solutechOne.voyager.dto.ManualSeatRequest;
import com.solutechOne.voyager.dto.SeatClassRangeRequest;
import com.solutechOne.voyager.dto.SeatRequest;
import com.solutechOne.voyager.model.Seat;
import com.solutechOne.voyager.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/V1/seats")
@Tag(name = "Seats", description = "Gestion des sièges")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @Operation(summary = "Créer un siège")
    @PostMapping
    public ResponseEntity<Seat> createSeat(@RequestBody SeatRequest request) {
        Seat seat = seatService.createSeat(request);
        return ResponseEntity.status(201).body(seat);
    }

    @Operation(summary = "Créer un siège manuellement")
    @PostMapping("/manual")
    public ResponseEntity<Seat> createManualSeat(@RequestBody ManualSeatRequest request) {
        Seat seat = seatService.createManualSeat(
                request.getMeansId(),
                request.getClassId(),
                request.getSeatOrderNumber(),
                request.getSeatReference()
        );

        return ResponseEntity.status(201).body(seat);
    }

    @Operation(summary = "Générer des sièges automatiquement")
    @PostMapping("/generate")
    public ResponseEntity<String> generateSeats(@RequestBody GenerateSeatRequest request) {
        return ResponseEntity.ok(
                seatService.generateSeats(
                        request.getMeansId(),
                        request.getTotalSeats(),
                        request.getClassId(),
                        request.getMode()
                )
        );
    }

    @Operation(summary = "Assigner des classes de sièges par intervalle")
    @PutMapping("/means/{meansId}/classes/ranges")
    public ResponseEntity<String> assignSeatClassesByRange(
            @PathVariable String meansId,
            @RequestBody List<SeatClassRangeRequest> ranges
    ) {
        return ResponseEntity.ok(
                seatService.assignSeatClassesByRange(meansId, ranges)
        );
    }

    @Operation(summary = "Obtenir tous les sièges")
    @GetMapping
    public ResponseEntity<List<Seat>> getAllSeats() {
        return ResponseEntity.ok(seatService.getAllSeats());
    }

    @Operation(summary = "Obtenir un siège par ID")
    @GetMapping("/{seatId}")
    public ResponseEntity<Seat> getSeatById(@PathVariable String seatId) {
        return seatService.getSeatById(seatId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Seat introuvable: " + seatId));
    }

    @Operation(summary = "Obtenir les sièges par moyen de transport")
    @GetMapping("/means/{meansId}")
    public ResponseEntity<List<Seat>> getSeatsByMeansId(@PathVariable String meansId) {
        return ResponseEntity.ok(seatService.getSeatsByMeansId(meansId));
    }

    @Operation(summary = "Obtenir les sièges par classe")
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<Seat>> getSeatsByClassId(@PathVariable String classId) {
        return ResponseEntity.ok(seatService.getSeatsByClassId(classId));
    }

    @Operation(summary = "Modifier un siège")
    @PutMapping("/{seatId}")
    public ResponseEntity<Seat> updateSeat(
            @PathVariable String seatId,
            @RequestBody Seat updatedSeat
    ) {
        return ResponseEntity.ok(seatService.updateSeat(seatId, updatedSeat));
    }

    @Operation(summary = "Supprimer un siège")
    @DeleteMapping("/{seatId}")
    public ResponseEntity<Void> deleteSeat(@PathVariable String seatId) {
        seatService.deleteSeat(seatId);
        return ResponseEntity.noContent().build();
    }
}