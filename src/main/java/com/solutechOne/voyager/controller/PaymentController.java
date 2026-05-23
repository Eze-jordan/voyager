package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.dto.PaymentCallbackRequest;
import com.solutechOne.voyager.dto.PaymentInitVoyagerRequest;
import com.solutechOne.voyager.dto.PaymentInitVoyagerResponse;
import com.solutechOne.voyager.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/V1/payments")
@Tag(name = "Payments", description = "Gestion des paiements, callbacks et vérification du statut provider")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Initier un paiement")
    @PostMapping("/initiate")
    public ResponseEntity<PaymentInitVoyagerResponse> initiate(@RequestBody PaymentInitVoyagerRequest request) {
        return ResponseEntity.ok(paymentService.initiatePayment(request));
    }

    @Operation(summary = "Recevoir le callback de paiement")
    @PostMapping("/callback")
    public ResponseEntity<PaymentInitVoyagerResponse> callback(@RequestBody PaymentCallbackRequest request) {
        PaymentInitVoyagerResponse response = paymentService.handlePaymentCallback(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Récupérer un paiement par référence")
    @GetMapping("/reference/{reference}")
    public ResponseEntity<PaymentInitVoyagerResponse> getByReference(@PathVariable String reference) {
        return ResponseEntity.ok(paymentService.getPaymentByReference(reference));
    }

    @Operation(summary = "Récupérer un paiement par ID")
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentInitVoyagerResponse> getByPaymentId(@PathVariable String paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

    @Operation(summary = "Vérifier le statut du paiement chez le provider")
    @GetMapping("/{paymentId}/status-provider")
    public ResponseEntity<String> checkProviderStatus(@PathVariable String paymentId) {
        return ResponseEntity.ok(paymentService.checkProviderStatus(paymentId));
    }
}