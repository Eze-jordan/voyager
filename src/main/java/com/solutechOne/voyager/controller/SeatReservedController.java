package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.model.SeatReserved;
import com.solutechOne.voyager.service.SeatReservedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/V1/seat-reserved")
public class SeatReservedController {

    private final SeatReservedService seatReservedService;

    public SeatReservedController(SeatReservedService seatReservedService) {
        this.seatReservedService = seatReservedService;
    }

    @GetMapping
    public ResponseEntity<List<SeatReserved>> getByDeparture(@RequestParam String departureId) {
        List<SeatReserved> seats = seatReservedService.getByDeparture(departureId);
        return ResponseEntity.ok(seats);
    }

    @PutMapping("/assign")
    public ResponseEntity<SeatReserved> assignSeat(
            @RequestParam String reservationId,
            @RequestParam String seatId
    ) {
        SeatReserved assigned = seatReservedService.assignSeat(reservationId, seatId);
        return ResponseEntity.ok(assigned);
    }

    @PutMapping("/confirm-boarding")
    public ResponseEntity<SeatReserved> confirmBoarding(@RequestParam String reservationId) {
        SeatReserved updated = seatReservedService.confirmBoarding(reservationId);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/cancel")
    public ResponseEntity<SeatReserved> cancelSeatAssignment(@RequestParam String reservationId) {
        SeatReserved updated = seatReservedService.cancelSeatAssignment(reservationId);
        return ResponseEntity.ok(updated);
    }
}