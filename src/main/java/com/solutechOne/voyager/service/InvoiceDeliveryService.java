package com.solutechOne.voyager.service;

import com.solutechOne.voyager.model.Invoice;
import com.solutechOne.voyager.repositories.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Service
public class InvoiceDeliveryService {

    private final InvoiceRepository invoiceRepository;
    private final NotificationService notificationService;

    public InvoiceDeliveryService(
            InvoiceRepository invoiceRepository,
            NotificationService notificationService
    ) {
        this.invoiceRepository = invoiceRepository;
        this.notificationService = notificationService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendInvoiceByEmail(String invoiceId) {

        Invoice invoice = invoiceRepository
                .findById(invoiceId)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Invoice introuvable : " + invoiceId
                        )
                );

        // Déjà envoyée : ne rien faire
        if (invoice.isEmailSent()) {
            return;
        }

        if (invoice.getBuyerEmail() == null
                || invoice.getBuyerEmail().isBlank()) {

            throw new IllegalStateException(
                    "Aucun email acheteur pour le Billet "
                            + invoice.getInvoiceNumber()
            );
        }

        if (invoice.getPdfPath() == null
                || invoice.getPdfPath().isBlank()) {

            throw new IllegalStateException(
                    "Aucun PDF généré pour le Billet "
                            + invoice.getInvoiceNumber()
            );
        }

        Path pdfPath = Path.of(invoice.getPdfPath());

        if (!Files.exists(pdfPath)) {
            throw new IllegalStateException(
                    "PDF introuvable : " + pdfPath
            );
        }

        notificationService.envoyerFacture(
                invoice,
                invoice.getPdfPath()
        );

        invoice.setEmailSent(true);
        invoice.setEmailSentAt(LocalDateTime.now());

        invoiceRepository.save(invoice);
    }
}