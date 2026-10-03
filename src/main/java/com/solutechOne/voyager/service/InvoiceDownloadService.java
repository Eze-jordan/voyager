package com.solutechOne.voyager.service;

import com.solutechOne.voyager.model.Invoice;
import com.solutechOne.voyager.repositories.InvoiceRepository;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class InvoiceDownloadService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceDownloadService(
            InvoiceRepository invoiceRepository
    ) {
        this.invoiceRepository = invoiceRepository;
    }

    public InvoiceDownload getByBasketId(String basketId) {

        Invoice invoice = invoiceRepository
                .findByBasketId(basketId)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Aucune facture disponible pour le panier : "
                                        + basketId
                        )
                );

        if (invoice.getPdfPath() == null
                || invoice.getPdfPath().isBlank()) {

            throw new IllegalStateException(
                    "Le PDF de la facture n'est pas encore disponible"
            );
        }

        Path path = Path.of(invoice.getPdfPath());

        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            throw new IllegalStateException(
                    "Le fichier PDF de la facture est introuvable"
            );
        }

        Resource resource =
                new FileSystemResource(path);

        return new InvoiceDownload(
                invoice.getInvoiceNumber() + ".pdf",
                resource
        );
    }

    public record InvoiceDownload(
            String filename,
            Resource resource
    ) {
    }
}