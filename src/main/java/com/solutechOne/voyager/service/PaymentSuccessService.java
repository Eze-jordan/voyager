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

    public PaymentSuccessService(
            PaymentTransactionRepository paymentRepository,
            ReservationRepository reservationRepository,
            BasketService basketService
    ) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.basketService = basketService;
    }

    @Transactional
    public PaymentTransaction traiterPaiementReussi(
            PaymentTransaction tx,
            String providerStatus
    ) {

        if (tx == null) {
            throw new IllegalArgumentException(
                    "Payment transaction is required"
            );
        }

        /*
         * Protection contre un double traitement :
         * callback + polling peuvent tous les deux détecter SUCCESS.
         */
        if (tx.getStatus() == PaymentStatus.SUCCESS) {
            return tx;
        }

        Basket basket = tx.getBasket();

        if (basket == null) {
            throw new IllegalStateException(
                    "Payment transaction is not linked to a basket"
            );
        }

        // 1. Transaction SUCCESS
        tx.setStatus(PaymentStatus.SUCCESS);

        if (providerStatus != null && !providerStatus.isBlank()) {
            tx.setCallbackRawStatus(providerStatus);
        }

        tx.setProviderMessage(
                "Paiement confirmé par le provider"
        );

        paymentRepository.save(tx);

        // 2. Panier PAYE
        basketService.markPaidByCallback(
                basket.getBasketId(),
                tx.getOperatorName(),
                tx.getExternalTransactionId(),
                tx.getCustomerAccountNumber()
        );

        // 3. Confirmation de toutes les réservations
        reservationRepository
                .findByBasket_BasketId(basket.getBasketId())
                .forEach(reservation -> {

                    if (reservation.getReservationConfirmed()
                            != ReservationStatus.YES) {

                        reservation.setReservationConfirmed(
                                ReservationStatus.YES
                        );

                        reservationRepository.save(reservation);
                    }
                });

        return tx;
    }
}