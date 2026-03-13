package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.dto.ReservationRequest;
import com.solutechOne.voyager.dto.ReservationResponse;
import com.solutechOne.voyager.model.Reservation;
import com.solutechOne.voyager.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/V1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

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

        // Conversion de la réservation en réponse simplifiée
        ReservationResponse response = new ReservationResponse(reservation);

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{reference}")
    public ResponseEntity<ReservationResponse> getReservationByReference(@PathVariable String reference) {
        Reservation reservation = reservationService.getReservationByReference(reference);

        // Retour simplifié pour la réponse
        ReservationResponse response = new ReservationResponse(reservation);

        return ResponseEntity.ok(response);
    }
}