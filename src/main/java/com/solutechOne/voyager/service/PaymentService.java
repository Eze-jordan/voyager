package com.solutechOne.voyager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solutechOne.voyager.dto.KycResponse;
import com.solutechOne.voyager.dto.PaymentCallbackRequest;
import com.solutechOne.voyager.dto.PaymentInitVoyagerRequest;
import com.solutechOne.voyager.dto.PaymentInitVoyagerResponse;
import com.solutechOne.voyager.dto.PaymentProviderInitRequest;
import com.solutechOne.voyager.enums.BasketStatus;
import com.solutechOne.voyager.enums.PaymentStatus;
import com.solutechOne.voyager.enums.ReservationStatus;
import com.solutechOne.voyager.integration.SolutechPaymentClient;
import com.solutechOne.voyager.model.Basket;
import com.solutechOne.voyager.model.PaymentTransaction;
import com.solutechOne.voyager.repositories.BasketRepository;
import com.solutechOne.voyager.repositories.PaymentTransactionRepository;
import com.solutechOne.voyager.repositories.ReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final BasketRepository basketRepository;
    private final PaymentTransactionRepository paymentRepository;
    private final SolutechPaymentClient paymentClient;
    private final ReservationRepository reservationRepository;
    private final BasketService basketService;
    private final ObjectMapper objectMapper;

    public PaymentService(
            BasketRepository basketRepository,
            PaymentTransactionRepository paymentRepository,
            SolutechPaymentClient paymentClient,
            ReservationRepository reservationRepository,
            BasketService basketService,
            ObjectMapper objectMapper
    ) {
        this.basketRepository = basketRepository;
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
        this.reservationRepository = reservationRepository;
        this.basketService = basketService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public PaymentInitVoyagerResponse initiatePayment(PaymentInitVoyagerRequest request) {
        validateInitRequest(request);

        Basket basket = basketRepository.findById(request.getBasketId())
                .orElseThrow(() -> new RuntimeException("Basket not found"));

        if (basket.getBasketStatus() == BasketStatus.PAYE) {
            throw new IllegalStateException("Basket already paid");
        }

        if (basket.getBasketStatus() == BasketStatus.ABANDONNE) {
            throw new IllegalStateException("Cannot pay an abandoned basket");
        }

        if (basket.getBasketTotalAmount() == null || basket.getBasketTotalAmount().signum() <= 0) {
            throw new IllegalStateException("Basket total amount must be greater than zero");
        }

        boolean alreadyRunning = paymentRepository.existsByBasket_BasketIdAndStatusIn(
                basket.getBasketId(),
                List.of(
                        PaymentStatus.CREATED,
                        PaymentStatus.KYC_PENDING,
                        PaymentStatus.KYC_OK,
                        PaymentStatus.INITIATED,
                        PaymentStatus.PENDING,
                        PaymentStatus.SUCCESS
                )
        );

        if (alreadyRunning) {
            throw new IllegalStateException("A payment already exists or is in progress for this basket");
        }

        PaymentTransaction tx = new PaymentTransaction();
        tx.setPaymentId("pay-" + UUID.randomUUID());
        tx.setBasket(basket);

        String reference = generateUniquePaymentReference();

        tx.setReference(reference);
        tx.setAppId(request.getAppId());
        tx.setOperatorName(request.getOperatorName());
        tx.setCustomerAccountNumber(request.getCustomerAccountNumber());
        tx.setAmount(basket.getBasketTotalAmount());
        tx.setStatus(PaymentStatus.CREATED);
        paymentRepository.save(tx);

        tx.setStatus(PaymentStatus.KYC_PENDING);
        paymentRepository.save(tx);

        KycResponse kyc = paymentClient.kyc(
                request.getAppId(),
                request.getCustomerAccountNumber()
        );

        if (kyc == null) {
            tx.setStatus(PaymentStatus.KYC_FAILED);
            tx.setProviderMessage("KYC response null");
            paymentRepository.save(tx);
            throw new IllegalStateException("KYC response null");
        }

        if (kyc.getErrorMessage() != null && !kyc.getErrorMessage().isBlank()) {
            tx.setStatus(PaymentStatus.KYC_FAILED);
            tx.setProviderMessage(kyc.getErrorMessage());
            paymentRepository.save(tx);
            throw new IllegalStateException("KYC error: " + kyc.getErrorMessage());
        }

        if (!kyc.isIs_active()) {
            tx.setStatus(PaymentStatus.KYC_FAILED);
            tx.setProviderMessage("Compte client inactif");
            paymentRepository.save(tx);
            throw new IllegalStateException("Compte client inactif");
        }

        tx.setStatus(PaymentStatus.KYC_OK);
        tx.setProviderMessage("KYC OK - " + kyc.getFull_name());
        paymentRepository.save(tx);

        PaymentProviderInitRequest providerBody = new PaymentProviderInitRequest(
                tx.getAmount(),
                tx.getReference(),
                request.getCustomerAccountNumber()
        );

        try {
            tx.setStatus(PaymentStatus.INITIATED);
            paymentRepository.save(tx);
            try {
                System.out.println("APP ID PAYMENT = " + request.getAppId());
                System.out.println("PROVIDER BODY JSON = " + objectMapper.writeValueAsString(providerBody));
            } catch (Exception e) {
                System.out.println("Impossible d'afficher le body provider: " + e.getMessage());
            }
            String initResponse = paymentClient.initPayment(request.getAppId(), providerBody);
            applyInitResponse(tx, basket, initResponse, request);

            return buildResponse(tx, basket);

        } catch (Exception firstEx) {
            if (!shouldRetry(firstEx)) {
                tx.setStatus(PaymentStatus.FAILED);
                tx.setProviderMessage(firstEx.getMessage());
                paymentRepository.save(tx);
                throw new RuntimeException("Payment initiation failed: " + firstEx.getMessage(), firstEx);
            }

            try {
                String retryResponse = paymentClient.initPayment(request.getAppId(), providerBody);
                applyInitResponse(tx, basket, retryResponse, request);

                return buildResponse(tx, basket);

            } catch (Exception retryEx) {
                tx.setStatus(PaymentStatus.FAILED);
                tx.setProviderMessage(retryEx.getMessage());
                paymentRepository.save(tx);
                basketService.markPaymentFailed(basket.getBasketId());
                throw new RuntimeException("Payment initiation failed after retry: " + retryEx.getMessage(), retryEx);
            }
        }
    }
    @Transactional
    public PaymentInitVoyagerResponse handlePaymentCallback(PaymentCallbackRequest callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback body is required");
        }

        String reference = normalize(callback.getReference());

        if (reference == null) {
            throw new IllegalArgumentException("Callback reference is required");
        }

        PaymentTransaction tx = paymentRepository.findByReference(reference)
                .orElseThrow(() -> new RuntimeException("Payment transaction not found"));

        String transactionId = normalize(callback.getTransactionId());
        String callbackStatus = normalize(callback.getStatus());
        String callbackMessage = normalize(callback.getMessage());

        if (transactionId != null) {
            tx.setExternalTransactionId(transactionId);
        }

        if (callbackStatus != null) {
            tx.setCallbackRawStatus(callbackStatus);
        }

        if (callbackMessage != null) {
            tx.setProviderMessage(callbackMessage);
        }

        Basket basket = tx.getBasket();
        String status = callbackStatus == null ? "" : callbackStatus.trim().toUpperCase();

        if ("SUCCESS".equals(status) || "PAID".equals(status) || "COMPLETED".equals(status)) {
            tx.setStatus(PaymentStatus.SUCCESS);

            basketService.markPaidByCallback(
                    basket.getBasketId(),
                    tx.getOperatorName(),
                    tx.getExternalTransactionId(),
                    tx.getCustomerAccountNumber()
            );

            reservationRepository.findByBasket_BasketId(basket.getBasketId())
                    .forEach(reservation -> {
                        reservation.setReservationConfirmed(ReservationStatus.YES);
                        reservationRepository.save(reservation);
                    });

        } else if ("FAILED".equals(status)) {
            tx.setStatus(PaymentStatus.FAILED);
            basketService.markPaymentFailed(basket.getBasketId());

        } else if ("CANCELLED".equals(status)) {
            tx.setStatus(PaymentStatus.CANCELLED);
            basketService.markPaymentFailed(basket.getBasketId());

        } else if ("EXPIRED".equals(status)) {
            tx.setStatus(PaymentStatus.EXPIRED);
            basketService.markPaymentFailed(basket.getBasketId());

        } else {
            tx.setStatus(PaymentStatus.PENDING);
        }

        paymentRepository.save(tx);

        return buildResponse(tx, basket);
    }
    private PaymentInitVoyagerResponse buildResponse(PaymentTransaction tx, Basket basket) {
        return new PaymentInitVoyagerResponse(
                tx.getPaymentId(),
                basket.getBasketId(),
                tx.getReference(),
                tx.getStatus().name(),
                tx.getAmount(),
                tx.getExternalTransactionId(),
                tx.getProviderMessage()
        );
    }

    private void validateInitRequest(PaymentInitVoyagerRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }

        if (request.getBasketId() == null || request.getBasketId().isBlank()) {
            throw new IllegalArgumentException("basketId is required");
        }

        if (request.getAppId() == null || request.getAppId().isBlank()) {
            throw new IllegalArgumentException("appId is required");
        }

        if (request.getOperatorName() == null || request.getOperatorName().isBlank()) {
            throw new IllegalArgumentException("operatorName is required");
        }

        if (request.getCustomerAccountNumber() == null || request.getCustomerAccountNumber().isBlank()) {
            throw new IllegalArgumentException("customerAccountNumber is required");
        }
    }

    private void applyInitResponse(
            PaymentTransaction tx,
            Basket basket,
            String rawJson,
            PaymentInitVoyagerRequest request
    ) {
        try {
            JsonNode root = objectMapper.readTree(rawJson);

            String providerStatus = readAny(root, "status", "state", "result");
            String transactionId = readAny(
                    root,
                    "reference_id",
                    "transactionId",
                    "transaction_id",
                    "providerTransactionId",
                    "reference"
            );            String message = extractMessage(rawJson, "Payment initiated");

            if (providerStatus != null) {
                String ps = providerStatus.trim().toUpperCase();

                if (ps.contains("FAILED") || ps.contains("ERROR")) {
                    tx.setStatus(PaymentStatus.FAILED);
                    tx.setProviderMessage(message);
                    paymentRepository.save(tx);
                    throw new IllegalStateException("Provider returned failed status: " + providerStatus);
                }
            }

            tx.setStatus(PaymentStatus.PENDING);
            tx.setExternalTransactionId(transactionId);
            tx.setProviderMessage(message);
            paymentRepository.save(tx);

            basketService.markPaymentPending(
                    basket.getBasketId(),
                    request.getOperatorName(),
                    transactionId,
                    request.getCustomerAccountNumber()
            );

        } catch (RuntimeException re) {
            throw re;

        } catch (Exception e) {
            tx.setStatus(PaymentStatus.FAILED);
            tx.setProviderMessage(e.getMessage());
            paymentRepository.save(tx);
            throw new RuntimeException("Unable to parse payment init response", e);
        }
    }

    @Transactional
    public PaymentInitVoyagerResponse getPaymentByReference(String reference) {
        PaymentTransaction tx = paymentRepository.findByReference(reference)
                .orElseThrow(() -> new RuntimeException("Payment transaction not found"));

        return buildResponse(tx, tx.getBasket());
    }

    @Transactional
    public PaymentInitVoyagerResponse getPaymentById(String paymentId) {
        PaymentTransaction tx = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment transaction not found"));

        return buildResponse(tx, tx.getBasket());
    }


    private String extractMessage(String rawJson, String fallback) {
        try {
            JsonNode root = objectMapper.readTree(rawJson);
            String message = readAny(root, "message", "label", "description", "detail");
            return message != null ? message : fallback;
        } catch (Exception e) {
            return fallback;
        }
    }

    private String readAny(JsonNode root, String... fieldNames) {
        for (String field : fieldNames) {
            JsonNode node = root.get(field);

            if (node != null && !node.isNull() && !node.asText().isBlank()) {
                return node.asText();
            }
        }

        return null;
    }

    private boolean shouldRetry(Exception ex) {
        String message = ex.getMessage();

        if (message == null) {
            return false;
        }

        String upper = message.toUpperCase();

        return upper.contains("502")
                || upper.contains("RENEWSECRET")
                || upper.contains("AUTHENTICATION_FAILED")
                || upper.contains("PVIT_RENEW_NETWORK_ERROR");
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
    private static final String PAYMENT_REFERENCE_PREFIX = "PEYREF";
    private static final int PAYMENT_REFERENCE_DIGITS = 10;
    private static final int MAX_REFERENCE_GENERATION_ATTEMPTS = 20;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private String generateUniquePaymentReference() {
        for (int attempt = 0; attempt < MAX_REFERENCE_GENERATION_ATTEMPTS; attempt++) {
            String reference = generatePaymentReferenceCandidate();

            if (paymentRepository.findByReference(reference).isEmpty()) {
                return reference;
            }
        }

        throw new IllegalStateException("Unable to generate unique payment reference");
    }

    private String generatePaymentReferenceCandidate() {
        StringBuilder reference = new StringBuilder(PAYMENT_REFERENCE_PREFIX);

        for (int i = 0; i < PAYMENT_REFERENCE_DIGITS; i++) {
            reference.append(SECURE_RANDOM.nextInt(10));
        }

        return reference.toString();
    }

    @Transactional
    public String checkProviderStatus(String paymentId) {
        PaymentTransaction tx = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment transaction not found"));

        if (tx.getExternalTransactionId() == null || tx.getExternalTransactionId().isBlank()) {
            throw new IllegalStateException("External transaction ID not available yet");
        }

        return paymentClient.paymentStatus(
                tx.getAppId(),
                tx.getReference()
        );
    }
}