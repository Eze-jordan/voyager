package com.solutechOne.voyager.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.solutechOne.voyager.model.Invoice;
import com.solutechOne.voyager.model.InvoiceLine;
import com.solutechOne.voyager.repositories.InvoiceRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class InvoicePdfService {

    private final InvoiceRepository invoiceRepository;

    /*
     * Répertoire configurable depuis application.properties.
     *
     * Par défaut :
     * ./invoices
     */
    @Value("${app.invoice.storage-path:./invoices}")
    private String invoiceStoragePath;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm");

    public InvoicePdfService(
            InvoiceRepository invoiceRepository
    ) {
        this.invoiceRepository = invoiceRepository;
    }

    // =========================================================
    // GÉNÉRATION DU PDF
    // =========================================================

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateInvoicePdf(Invoice invoice)  {

        if (invoice == null) {
            throw new IllegalArgumentException(
                    "Invoice is required"
            );
        }

        if (invoice.getInvoiceId() == null) {
            throw new IllegalArgumentException(
                    "Invoice ID is required"
            );
        }

        if (invoice.getInvoiceNumber() == null
                || invoice.getInvoiceNumber().isBlank()) {

            throw new IllegalArgumentException(
                    "Invoice number is required"
            );
        }

        /*
         * Si le PDF existe déjà, on ne le recrée pas.
         */
        if (invoice.getPdfPath() != null
                && !invoice.getPdfPath().isBlank()) {

            Path existingPath =
                    Paths.get(invoice.getPdfPath());

            if (Files.exists(existingPath)) {
                return existingPath.toAbsolutePath().toString();
            }
        }

        try {

            // =================================================
            // DOSSIER PAR ANNÉE
            // =================================================

            String year =
                    invoice.getInvoiceDate() != null
                            ? String.valueOf(
                            invoice
                                    .getInvoiceDate()
                                    .getYear()
                    )
                            : String.valueOf(
                            java.time.LocalDate.now().getYear()
                    );

            Path directory =
                    Paths.get(
                            invoiceStoragePath,
                            year
                    );

            Files.createDirectories(directory);

            // =================================================
            // NOM DU FICHIER
            // =================================================

            String safeInvoiceNumber =
                    sanitizeFileName(
                            invoice.getInvoiceNumber()
                    );

            Path pdfPath =
                    directory.resolve(
                            safeInvoiceNumber + ".pdf"
                    );

            // =================================================
            // DOCUMENT
            // =================================================

            Document document =
                    new Document(
                            PageSize.A4,
                            40,
                            40,
                            40,
                            40
                    );

            PdfWriter.getInstance(
                    document,
                    new FileOutputStream(
                            pdfPath.toFile()
                    )
            );

            document.open();

            // =================================================
            // POLICES
            // =================================================

            Font titleFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            20
                    );

            Font sectionFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            11
                    );

            Font normalFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            9
                    );

            Font smallFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            8
                    );

            Font boldFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            9
                    );

            Font totalFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            12
                    );

            // =================================================
            // EN-TÊTE
            // =================================================

            PdfPTable header =
                    new PdfPTable(2);

            header.setWidthPercentage(100);
            header.setWidths(
                    new float[]{60f, 40f}
            );

            PdfPCell companyCell =
                    new PdfPCell();

            companyCell.setBorder(
                    Rectangle.NO_BORDER
            );

            companyCell.addElement(
                    new Paragraph(
                            valueOrEmpty(
                                    invoice.getCompanyName()
                            ),
                            sectionFont
                    )
            );

            if (hasText(invoice.getCompanyAddress())) {
                companyCell.addElement(
                        new Paragraph(
                                invoice.getCompanyAddress(),
                                normalFont
                        )
                );
            }

            if (hasText(invoice.getCompanyPhone())) {
                companyCell.addElement(
                        new Paragraph(
                                "Tél. : "
                                        + invoice.getCompanyPhone(),
                                normalFont
                        )
                );
            }

            if (hasText(invoice.getCompanyEmail())) {
                companyCell.addElement(
                        new Paragraph(
                                "Email : "
                                        + invoice.getCompanyEmail(),
                                normalFont
                        )
                );
            }

            if (hasText(invoice.getCompanyNif())) {
                companyCell.addElement(
                        new Paragraph(
                                "NIF : "
                                        + invoice.getCompanyNif(),
                                normalFont
                        )
                );
            }

            if (hasText(invoice.getCompanyRccm())) {
                companyCell.addElement(
                        new Paragraph(
                                "RCCM : "
                                        + invoice.getCompanyRccm(),
                                normalFont
                        )
                );
            }

            if (hasText(invoice.getCompanyAggrement())) {
                companyCell.addElement(
                        new Paragraph(
                                "Agrément : "
                                        + invoice.getCompanyAggrement(),
                                normalFont
                        )
                );
            }

            header.addCell(companyCell);

            // =================================================
            // TITRE FACTURE
            // =================================================

            PdfPCell invoiceTitleCell =
                    new PdfPCell();

            invoiceTitleCell.setBorder(
                    Rectangle.NO_BORDER
            );

            invoiceTitleCell.setHorizontalAlignment(
                    Element.ALIGN_RIGHT
            );

            Paragraph title =
                    new Paragraph(
                            "FACTURE",
                            titleFont
                    );

            title.setAlignment(
                    Element.ALIGN_RIGHT
            );

            invoiceTitleCell.addElement(title);

            Paragraph number =
                    new Paragraph(
                            invoice.getInvoiceNumber(),
                            boldFont
                    );

            number.setAlignment(
                    Element.ALIGN_RIGHT
            );

            invoiceTitleCell.addElement(number);

            if (invoice.getInvoiceDate() != null) {

                Paragraph date =
                        new Paragraph(
                                "Date : "
                                        + invoice
                                        .getInvoiceDate()
                                        .format(DATE_FORMAT),
                                normalFont
                        );

                date.setAlignment(
                        Element.ALIGN_RIGHT
                );

                invoiceTitleCell.addElement(date);
            }

            header.addCell(invoiceTitleCell);

            document.add(header);

            document.add(
                    new Paragraph(" ")
            );

            // =================================================
            // CLIENT
            // =================================================

            document.add(
                    new Paragraph(
                            "CLIENT",
                            sectionFont
                    )
            );

            if (hasText(invoice.getBuyerEmail())) {
                document.add(
                        new Paragraph(
                                "Email : "
                                        + invoice.getBuyerEmail(),
                                normalFont
                        )
                );
            }

            if (hasText(invoice.getBuyerPhone())) {
                document.add(
                        new Paragraph(
                                "Téléphone : "
                                        + invoice.getBuyerPhone(),
                                normalFont
                        )
                );
            }

            document.add(
                    new Paragraph(" ")
            );

            // =================================================
            // TABLEAU DES BILLETS
            // =================================================

            PdfPTable linesTable =
                    new PdfPTable(5);

            linesTable.setWidthPercentage(100);

            linesTable.setWidths(
                    new float[]{
                            23f,
                            29f,
                            16f,
                            14f,
                            18f
                    }
            );

            addHeaderCell(
                    linesTable,
                    "Passager",
                    boldFont
            );

            addHeaderCell(
                    linesTable,
                    "Trajet",
                    boldFont
            );

            addHeaderCell(
                    linesTable,
                    "Date",
                    boldFont
            );

            addHeaderCell(
                    linesTable,
                    "Classe",
                    boldFont
            );

            addHeaderCell(
                    linesTable,
                    "Montant",
                    boldFont
            );

            if (invoice.getLines() != null) {

                for (InvoiceLine line :
                        invoice.getLines()) {

                    // PASSAGER

                    String passenger =
                            joinNames(
                                    line.getPassengerFirstname(),
                                    line.getPassengerName()
                            );

                    addBodyCell(
                            linesTable,
                            passenger,
                            smallFont
                    );

                    // TRAJET

                    String route =
                            valueOrEmpty(
                                    line.getDepartureCity()
                            )
                                    + " → "
                                    + valueOrEmpty(
                                    line.getArrivalCity()
                            );

                    addBodyCell(
                            linesTable,
                            route,
                            smallFont
                    );

                    // DATE / HEURE

                    StringBuilder travelDate =
                            new StringBuilder();

                    if (line.getDepartureDate() != null) {

                        travelDate.append(
                                line.getDepartureDate()
                                        .format(DATE_FORMAT)
                        );
                    }

                    if (line.getDepartureTime() != null) {

                        if (!travelDate.isEmpty()) {
                            travelDate.append(" ");
                        }

                        travelDate.append(
                                line.getDepartureTime()
                                        .format(TIME_FORMAT)
                        );
                    }

                    addBodyCell(
                            linesTable,
                            travelDate.toString(),
                            smallFont
                    );

                    // CLASSE

                    addBodyCell(
                            linesTable,
                            valueOrEmpty(
                                    line.getTravelClass()
                            ),
                            smallFont
                    );

                    // PRIX

                    addRightBodyCell(
                            linesTable,
                            formatAmount(
                                    line.getUnitPrice()
                            ),
                            smallFont
                    );
                }
            }

            document.add(linesTable);

            document.add(
                    new Paragraph(" ")
            );

            // =================================================
            // TOTAUX
            // =================================================

            PdfPTable totals =
                    new PdfPTable(2);

            totals.setWidthPercentage(45);
            totals.setHorizontalAlignment(
                    Element.ALIGN_RIGHT
            );

            totals.setWidths(
                    new float[]{55f, 45f}
            );

            addTotalRow(
                    totals,
                    "Sous-total",
                    formatAmount(
                            invoice.getBasketAmount()
                    ),
                    normalFont
            );

            addTotalRow(
                    totals,
                    "Frais",
                    formatAmount(
                            invoice.getFeesAmount()
                    ),
                    normalFont
            );

            addTotalRow(
                    totals,
                    "TOTAL PAYÉ",
                    formatAmount(
                            invoice.getTotalAmount()
                    ),
                    totalFont
            );

            document.add(totals);

            document.add(
                    new Paragraph(" ")
            );

            // =================================================
            // INFORMATIONS DE PAIEMENT
            // =================================================

            document.add(
                    new Paragraph(
                            "PAIEMENT",
                            sectionFont
                    )
            );

            if (hasText(invoice.getPaymentOperator())) {

                document.add(
                        new Paragraph(
                                "Opérateur : "
                                        + invoice.getPaymentOperator(),
                                normalFont
                        )
                );
            }

            if (hasText(invoice.getPaymentReference())) {

                document.add(
                        new Paragraph(
                                "Référence : "
                                        + invoice.getPaymentReference(),
                                normalFont
                        )
                );
            }

            if (hasText(
                    invoice.getProviderTransactionId()
            )) {

                document.add(
                        new Paragraph(
                                "Transaction : "
                                        + invoice
                                        .getProviderTransactionId(),
                                normalFont
                        )
                );
            }

            if (invoice.getPaymentDate() != null) {

                document.add(
                        new Paragraph(
                                "Date de paiement : "
                                        + invoice
                                        .getPaymentDate()
                                        .format(
                                                DATE_TIME_FORMAT
                                        ),
                                normalFont
                        )
                );
            }

            document.add(
                    new Paragraph(" ")
            );

            // =================================================
            // BAS DE PAGE
            // =================================================

            Paragraph footer =
                    new Paragraph(
                            "Facture générée automatiquement après confirmation du paiement.",
                            smallFont
                    );

            footer.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(footer);

            document.close();

            // =================================================
            // ENREGISTREMENT DU CHEMIN
            // =================================================

            String absolutePath =
                    pdfPath
                            .toAbsolutePath()
                            .normalize()
                            .toString();

            invoice.setPdfPath(
                    absolutePath
            );

            invoiceRepository.save(invoice);

            return absolutePath;

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Impossible de générer le PDF de la facture "
                            + invoice.getInvoiceNumber(),
                    exception
            );
        }
    }

    // =========================================================
    // TABLE HELPERS
    // =========================================================

    private void addHeaderCell(
            PdfPTable table,
            String value,
            Font font
    ) {

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(
                                value,
                                font
                        )
                );

        cell.setPadding(7);
        cell.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );

        table.addCell(cell);
    }

    private void addBodyCell(
            PdfPTable table,
            String value,
            Font font
    ) {

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(
                                valueOrEmpty(value),
                                font
                        )
                );

        cell.setPadding(6);
        cell.setVerticalAlignment(
                Element.ALIGN_MIDDLE
        );

        table.addCell(cell);
    }

    private void addRightBodyCell(
            PdfPTable table,
            String value,
            Font font
    ) {

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(
                                valueOrEmpty(value),
                                font
                        )
                );

        cell.setPadding(6);

        cell.setHorizontalAlignment(
                Element.ALIGN_RIGHT
        );

        table.addCell(cell);
    }

    private void addTotalRow(
            PdfPTable table,
            String label,
            String value,
            Font font
    ) {

        PdfPCell labelCell =
                new PdfPCell(
                        new Phrase(
                                label,
                                font
                        )
                );

        labelCell.setPadding(6);

        table.addCell(labelCell);

        PdfPCell valueCell =
                new PdfPCell(
                        new Phrase(
                                value,
                                font
                        )
                );

        valueCell.setPadding(6);

        valueCell.setHorizontalAlignment(
                Element.ALIGN_RIGHT
        );

        table.addCell(valueCell);
    }

    // =========================================================
    // FORMAT MONTANT
    // =========================================================

    private String formatAmount(
            BigDecimal amount
    ) {

        if (amount == null) {
            amount = BigDecimal.ZERO;
        }

        NumberFormat formatter =
                NumberFormat.getNumberInstance(
                        Locale.FRENCH
                );

        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(2);

        return formatter.format(amount)
                + " FCFA";
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private String valueOrEmpty(
            String value
    ) {

        return value != null
                ? value
                : "";
    }

    private boolean hasText(
            String value
    ) {

        return value != null
                && !value.isBlank();
    }

    private String joinNames(
            String firstname,
            String name
    ) {

        String result =
                (
                        valueOrEmpty(firstname)
                                + " "
                                + valueOrEmpty(name)
                ).trim();

        return result.isBlank()
                ? "-"
                : result;
    }

    private String sanitizeFileName(
            String value
    ) {

        return value.replaceAll(
                "[^a-zA-Z0-9._-]",
                "_"
        );
    }
}