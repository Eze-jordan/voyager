package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.model.Seat;
import com.solutechOne.voyager.service.SeatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/V1/seats")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @PostMapping
    public ResponseEntity<Seat> createSeat(@RequestParam String meansId,
                                           @RequestParam(required = false) String classId,
                                           @RequestBody Seat seat) {
        return ResponseEntity.status(201).body(seatService.createSeat(meansId, classId, seat));
    }

    @GetMapping("/{seatId}")
    public ResponseEntity<Seat> getSeatById(@PathVariable String seatId) {
        return seatService.getSeatById(seatId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Seat>> getAllSeats() {
        return ResponseEntity.ok(seatService.getAllSeats());
    }

    @GetMapping("/by-means/{meansId}")
    public ResponseEntity<List<Seat>> getSeatsByMeansId(@PathVariable String meansId) {
        return ResponseEntity.ok(seatService.getSeatsByMeansId(meansId));
    }

    @GetMapping("/by-class/{classId}")
    public ResponseEntity<List<Seat>> getSeatsByClassId(@PathVariable String classId) {
        return ResponseEntity.ok(seatService.getSeatsByClassId(classId));
    }

    @PutMapping("/{seatId}")
    public ResponseEntity<Seat> updateSeat(@PathVariable String seatId, @RequestBody Seat seat) {
        return ResponseEntity.ok(seatService.updateSeat(seatId, seat));
    }

    @DeleteMapping("/{seatId}")
    public ResponseEntity<Void> deleteSeat(@PathVariable String seatId) {
        seatService.deleteSeat(seatId);
        return ResponseEntity.noContent().build();
    }

    public static class SeatGenerateRequest {
        public String meansId;
        public Integer totalSeats;
        public String classId;
        public String mode;
        public Boolean reset;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generate(@RequestBody SeatGenerateRequest req) {
        SeatService.SeatGenerationMode mode = SeatService.SeatGenerationMode.valueOf(req.mode.toUpperCase());
        boolean reset = req.reset != null && req.reset;

        String result = seatService.generateSeats(req.meansId, req.totalSeats, req.classId, mode, reset);
        return ResponseEntity.ok(result);
    }
}