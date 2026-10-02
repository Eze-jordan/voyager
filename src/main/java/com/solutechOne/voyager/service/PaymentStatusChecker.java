package com.solutechOne.voyager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solutechOne.voyager.enums.PaymentStatus;
import com.solutechOne.voyager.integration.SolutechPaymentClient;
import com.solutechOne.voyager.model.PaymentTransaction;
import com.solutechOne.voyager.repositories.PaymentTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PaymentStatusChecker {

    private final PaymentTransactionRepository paymentRepository;
    private final SolutechPaymentClient paymentClient;
    private final BasketService basketService;
    private final ObjectMapper objectMapper;
    private final PaymentSuccessService paymentSuccessService;

    public PaymentStatusChecker(
            PaymentTransactionRepository paymentRepository,
            SolutechPaymentClient paymentClient,
            BasketService basketService,
            ObjectMapper objectMapper,
            PaymentSuccessService paymentSuccessService
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
        this.basketService = basketService;
        this.objectMapper = objectMapper;
        this.paymentSuccessService = paymentSuccessService;
    }

    /**
     * @return true si le paiement est arrivé dans un état final.
     *         false s'il faut continuer les vérifications.
     */
    @Transactional
    public boolean check(String paymentId) {

        PaymentTransaction tx = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Payment transaction not found: " + paymentId
                        )
                );

        // ==============================================
        // DÉJÀ TERMINÉ
        // ==============================================

        if (tx.getStatus() == PaymentStatus.SUCCESS
                || tx.getStatus() == PaymentStatus.FAILED
                || tx.getStatus() == PaymentStatus.CANCELLED
                || tx.getStatus() == PaymentStatus.EXPIRED) {

            return true;
        }

        // ==============================================
        // INTERROGATION DU PROVIDER
        // ==============================================

        String rawResponse = paymentClient.paymentStatus(
                tx.getAppId(),
                tx.getReference()
        );

        try {

            JsonNode root = objectMapper.readTree(rawResponse);

            String providerStatus = readAny(
                    root,
                    "status",
                    "state",
                    "result"
            );

            if (providerStatus == null) {
                return false;
            }

            String status = providerStatus
                    .trim()
                    .toUpperCase();

            tx.setCallbackRawStatus(providerStatus);

            // ==============================================
            // SUCCESS
            // ==============================================

            if ("SUCCESS".equals(status)
                    || "PAID".equals(status)
                    || "COMPLETED".equals(status)) {

                paymentSuccessService.traiterPaiementReussi(
                        tx,
                        providerStatus
                );

                return true;
            }

            // ==============================================
            // FAILED
            // ==============================================

            if ("FAILED".equals(status)) {

                tx.setStatus(PaymentStatus.FAILED);

                tx.setProviderMessage(
                        "Paiement refusé par le provider"
                );

                paymentRepository.save(tx);

                basketService.markPaymentFailed(
                        tx.getBasket().getBasketId()
                );

                return true;
            }

            // ==============================================
            // CANCELLED
            // ==============================================

            if ("CANCELLED".equals(status)) {

                tx.setStatus(PaymentStatus.CANCELLED);

                tx.setProviderMessage(
                        "Paiement annulé par le provider"
                );

                paymentRepository.save(tx);

                basketService.markPaymentFailed(
                        tx.getBasket().getBasketId()
                );

                return true;
            }

            // ==============================================
            // EXPIRED
            // ==============================================

            if ("EXPIRED".equals(status)) {

                tx.setStatus(PaymentStatus.EXPIRED);

                tx.setProviderMessage(
                        "Paiement expiré"
                );

                paymentRepository.save(tx);

                basketService.markPaymentFailed(
                        tx.getBasket().getBasketId()
                );

                return true;
            }

            // ==============================================
            // TOUJOURS PENDING
            // ==============================================

            tx.setStatus(PaymentStatus.PENDING);

            tx.setProviderMessage(
                    "Paiement toujours en attente de confirmation"
            );

            paymentRepository.save(tx);

            return false;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to parse provider payment status: "
                            + e.getMessage(),
                    e
            );
        }
    }

    private String readAny(
            JsonNode root,
            String... fieldNames
    ) {

        for (String field : fieldNames) {

            JsonNode node = root.get(field);

            if (node != null
                    && !node.isNull()
                    && !node.asText().isBlank()) {

                return node.asText();
            }
        }

        return null;
    }
}