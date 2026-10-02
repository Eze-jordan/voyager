package com.solutechOne.voyager.repositories;

import com.solutechOne.voyager.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository
        extends JpaRepository<Invoice, String> {

    Optional<Invoice> findByPaymentId(String paymentId);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    Optional<Invoice> findByBasketId(String basketId);

    boolean existsByPaymentId(String paymentId);
}