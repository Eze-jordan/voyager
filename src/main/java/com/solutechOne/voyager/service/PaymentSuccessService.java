package com.solutechOne.voyager.service;

import com.solutechOne.voyager.enums.PaymentStatus;
import com.solutechOne.voyager.enums.ReservationStatus;
import com.solutechOne.voyager.model.Basket;
import com.solutechOne.voyager.model.PaymentTransaction;
import com.solutechOne.voyager.repositories.PaymentTransactionRepository;
import com.solutechOne.voyager.repositories.ReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PaymentSuccessService {

    private final PaymentTransactionRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final BasketService basketService;
    private final InvoiceService invoiceService;

    public PaymentSuccessService(
            PaymentTransactionRepository paymentRepository,
            ReservationRepository reservationRepository,
            BasketService basketService,
            InvoiceService invoiceService
    ) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.basketService = basketService;
        this.invoiceService = invoiceService;
    }

    @Transactional
    public PaymentTransaction traiterPaiementReussi(
            PaymentTransaction tx,
            String providerStatus
    ) {

        // =====================================================
        // VALIDATION
        // =====================================================

        if (tx == null) {
            throw new IllegalArgumentException(
                    "Payment transaction is required"
            );
        }

        if (tx.getPaymentId() == null
                || tx.getPaymentId().isBlank()) {

            throw new IllegalArgumentException(
                    "Payment ID is required"
            );
        }

        /*
         * IMPORTANT :
         *
         * On ne travaille plus directement avec l'instance
         * PaymentTransaction reçue.
         *
         * On recharge la transaction depuis PostgreSQL
         * avec un verrou PESSIMISTIC_WRITE.
         */
        PaymentTransaction lockedTx =
                paymentRepository
                        .findByIdForUpdate(
                                tx.getPaymentId()
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Payment transaction not found: "
                                                + tx.getPaymentId()
                                )
                        );

        // =====================================================
        // PANIER
        // =====================================================

        Basket basket =
                lockedTx.getBasket();

        if (basket == null) {
            throw new IllegalStateException(
                    "Payment transaction is not linked to a basket"
            );
        }

        // =====================================================
        // DÉJÀ SUCCESS
        // =====================================================

        if (lockedTx.getStatus()
                == PaymentStatus.SUCCESS) {

            /*
             * Le paiement est déjà traité.
             *
             * Mais on vérifie quand même que la facture existe.
             * Cela permet de réparer un traitement partiel.
             */
            invoiceService.createInvoiceIfAbsent(
                    lockedTx
            );

            return lockedTx;
        }

        // =====================================================
        // 1. TRANSACTION SUCCESS
        // =====================================================

        lockedTx.setStatus(
                PaymentStatus.SUCCESS
        );

        if (providerStatus != null
                && !providerStatus.isBlank()) {

            lockedTx.setCallbackRawStatus(
                    providerStatus
            );
        }

        lockedTx.setProviderMessage(
                "Paiement confirmé par le provider"
        );

        paymentRepository.save(
                lockedTx
        );

        // =====================================================
        // 2. PANIER PAYÉ
        // =====================================================

        basketService.markPaidByCallback(
                basket.getBasketId(),
                lockedTx.getOperatorName(),
                lockedTx.getExternalTransactionId(),
                lockedTx.getCustomerAccountNumber()
        );

        // =====================================================
        // 3. CONFIRMATION DES RÉSERVATIONS
        // =====================================================

        reservationRepository
                .findByBasket_BasketId(
                        basket.getBasketId()
                )
                .forEach(reservation -> {

                    if (reservation.getReservationConfirmed()
                            != ReservationStatus.YES) {

                        reservation.setReservationConfirmed(
                                ReservationStatus.YES
                        );

                        reservationRepository.save(
                                reservation
                        );
                    }
                });

        // =====================================================
        // 4. FACTURE
        // =====================================================

        invoiceService.createInvoiceIfAbsent(
                lockedTx
        );

        return lockedTx;
    }
}