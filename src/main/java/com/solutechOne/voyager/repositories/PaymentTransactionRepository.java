package com.solutechOne.voyager.repositories;

import com.solutechOne.voyager.enums.PaymentStatus;
import com.solutechOne.voyager.model.PaymentTransaction;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface PaymentTransactionRepository
        extends JpaRepository<PaymentTransaction, String> {

    Optional<PaymentTransaction> findByReference(String reference);

    Optional<PaymentTransaction> findByExternalTransactionId(
            String externalTransactionId
    );

    boolean existsByBasket_BasketIdAndStatusIn(
            String basketId,
            Collection<PaymentStatus> statuses
    );

    Optional<PaymentTransaction> findTopByBasket_BasketIdOrderByCreatedAtDesc(
            String basketId
    );

    // =========================================================
    // VERROU POUR TRAITEMENT SUCCESS
    // =========================================================

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT p
            FROM PaymentTransaction p
            WHERE p.paymentId = :paymentId
            """)
    Optional<PaymentTransaction> findByIdForUpdate(
            @Param("paymentId") String paymentId
    );
}