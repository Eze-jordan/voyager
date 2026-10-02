package com.solutechOne.voyager.event;

import com.solutechOne.voyager.model.Invoice;
import com.solutechOne.voyager.repositories.InvoiceRepository;
import com.solutechOne.voyager.service.InvoiceDeliveryService;
import com.solutechOne.voyager.service.InvoicePdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class InvoiceCreatedListener {

    private static final Logger log =
            LoggerFactory.getLogger(InvoiceCreatedListener.class);

    private final InvoiceRepository invoiceRepository;
    private final InvoicePdfService invoicePdfService;
    private final InvoiceDeliveryService invoiceDeliveryService;

    public InvoiceCreatedListener(
            InvoiceRepository invoiceRepository,
            InvoicePdfService invoicePdfService,
            InvoiceDeliveryService invoiceDeliveryService
    ) {
        this.invoiceRepository = invoiceRepository;
        this.invoicePdfService = invoicePdfService;
        this.invoiceDeliveryService = invoiceDeliveryService;
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleInvoiceCreated(
            InvoiceCreatedEvent event
    ) {

        log.info(
                ">>> AFTER_COMMIT reçu pour invoiceId={}",
                event.invoiceId()
        );

        try {

            // 1. Recharger la facture enregistrée
            Invoice invoice =
                    invoiceRepository
                            .findById(event.invoiceId())
                            .orElseThrow(
                                    () -> new IllegalStateException(
                                            "Invoice introuvable : "
                                                    + event.invoiceId()
                                    )
                            );

            log.info(
                    ">>> Invoice trouvée : {}",
                    invoice.getInvoiceNumber()
            );

            // 2. Générer le PDF
            String pdfPath =
                    invoicePdfService.generateInvoicePdf(invoice);

            log.info(
                    ">>> PDF facture généré : {}",
                    pdfPath
            );

            // 3. Envoyer le PDF par email
            invoiceDeliveryService.sendInvoiceByEmail(
                    invoice.getInvoiceId()
            );

            log.info(
                    ">>> Facture {} envoyée par email à {}",
                    invoice.getInvoiceNumber(),
                    invoice.getBuyerEmail()
            );

        } catch (Exception e) {

            log.error(
                    ">>> ERREUR traitement facture invoiceId={}",
                    event.invoiceId(),
                    e
            );
        }
    }
}