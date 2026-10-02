package com.solutechOne.voyager.repositories;

import com.solutechOne.voyager.model.InvoiceLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceLineRepository
        extends JpaRepository<InvoiceLine, String> {

    List<InvoiceLine> findByInvoice_InvoiceId(
            String invoiceId
    );
}