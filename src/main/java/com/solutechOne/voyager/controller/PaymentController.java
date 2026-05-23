package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.dto.PaymentCallbackRequest;
import com.solutechOne.voyager.dto.PaymentInitVoyagerRequest;
import com.solutechOne.voyager.dto.PaymentInitVoyagerResponse;
import com.solutechOne.voyager.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/V1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<PaymentInitVoyagerResponse> initiate(@RequestBody PaymentInitVoyagerRequest request) {
        return ResponseEntity.ok(paymentService.initiatePayment(request));
    }

    @PostMapping("/callback")
    public ResponseEntity<Void> callback(@RequestBody PaymentCallbackRequest request) {
        paymentService.handlePaymentCallback(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<PaymentInitVoyagerResponse> getByReference(@PathVariable String reference) {
        return ResponseEntity.ok(paymentService.getPaymentByReference(reference));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentInitVoyagerResponse> getByPaymentId(@PathVariable String paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }
}