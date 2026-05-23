package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.dto.ReservationRequest;
import com.solutechOne.voyager.dto.ReservationResponse;
import com.solutechOne.voyager.model.Reservation;
import com.solutechOne.voyager.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/V1/reservations")
@Tag(name = "Reservations", description = "Gestion des réservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "Créer une réservation")
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request) {
        Reservation reservation = reservationService.createReservation(
                request.getTravelId(),
                request.getPassengerName(),
                request.getPassengerFirstname(),
                request.getPassengerDateOfBirth(),
                request.getPassengerSex(),
                request.getPassengerNationality(),
                request.getPassengerMail(),
                request.getPassengerPhone(),
                request.getPassengerWhatsapp(),
                request.getTicketId()
        );

        return ResponseEntity.status(201).body(new ReservationResponse(reservation));
    }

    @Operation(summary = "Obtenir une réservation par référence")
    @GetMapping("/{reference}")
    public ResponseEntity<ReservationResponse> getReservationByReference(@PathVariable String reference) {
        Reservation reservation = reservationService.getReservationByReference(reference);
        return ResponseEntity.ok(new ReservationResponse(reservation));
    }
}