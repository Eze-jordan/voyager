package com.solutechOne.voyager.repositories;

import com.solutechOne.voyager.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, String> {
    Optional<PaymentTransaction> findByReference(String reference);
    Optional<PaymentTransaction> findByExternalTransactionId(String externalTransactionId);
    boolean existsByBasket_BasketIdAndStatusIn(String basketId, java.util.Collection<com.solutechOne.voyager.enums.PaymentStatus> statuses);
}