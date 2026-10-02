package com.solutechOne.voyager.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "invoices",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_invoice_payment",
                        columnNames = "payment_id"
                ),
                @UniqueConstraint(
                        name = "uk_invoice_number",
                        columnNames = "invoice_number"
                )
        },
        indexes = {
                @Index(
                        name = "idx_invoice_basket",
                        columnList = "basket_id"
                ),
                @Index(
                        name = "idx_invoice_company",
                        columnList = "company_id"
                ),
                @Index(
                        name = "idx_invoice_date",
                        columnList = "invoice_date"
                )
        }
)
public class Invoice {

    @Id
    @Column(
            name = "invoice_id",
            nullable = false,
            length = 60,
            updatable = false
    )
    private String invoiceId;

    @Column(
            name = "invoice_number",
            nullable = false,
            length = 50,
            unique = true,
            updatable = false
    )
    private String invoiceNumber;

    // =========================================================
    // IDENTIFIANTS D'ORIGINE
    // =========================================================

    @Column(
            name = "payment_id",
            nullable = false,
            length = 100,
            unique = true,
            updatable = false
    )
    private String paymentId;

    @Column(
            name = "basket_id",
            nullable = false,
            length = 60,
            updatable = false
    )
    private String basketId;

    @Column(
            name = "company_id",
            nullable = false,
            length = 60,
            updatable = false
    )
    private String companyId;

    // =========================================================
    // SNAPSHOT COMPANY
    // =========================================================

    @Column(name = "company_name", length = 100)
    private String companyName;

    @Column(name = "company_address", length = 255)
    private String companyAddress;

    @Column(name = "company_email", length = 100)
    private String companyEmail;

    @Column(name = "company_phone", length = 30)
    private String companyPhone;

    @Column(name = "company_nif", length = 50)
    private String companyNif;

    @Column(name = "company_rccm", length = 50)
    private String companyRccm;

    @Column(name = "company_aggrement", length = 100)
    private String companyAggrement;

    @Column(name = "company_logo", length = 500)
    private String companyLogo;

    // =========================================================
    // ACHETEUR
    // =========================================================

    @Column(name = "buyer_email", length = 100)
    private String buyerEmail;

    @Column(name = "buyer_phone", length = 30)
    private String buyerPhone;

    @Column(name = "buyer_whatsapp", length = 30)
    private String buyerWhatsapp;

    // =========================================================
    // MONTANTS
    // =========================================================

    @Column(
            name = "basket_amount",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal basketAmount;

    @Column(
            name = "fees_amount",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal feesAmount;

    @Column(
            name = "total_amount",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal totalAmount;

    // =========================================================
    // PAIEMENT
    // =========================================================

    @Column(name = "payment_reference", length = 50)
    private String paymentReference;

    @Column(name = "provider_transaction_id", length = 100)
    private String providerTransactionId;

    @Column(name = "payment_operator", length = 50)
    private String paymentOperator;

    @Column(name = "payment_account", length = 50)
    private String paymentAccount;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    // =========================================================
    // FACTURE
    // =========================================================

    @Column(
            name = "invoice_date",
            nullable = false,
            updatable = false
    )
    private LocalDateTime invoiceDate;

    @Column(name = "pdf_path", length = 500)
    private String pdfPath;

    @Column(name = "email_sent", nullable = false)
    private boolean emailSent = false;

    @Column(name = "email_sent_at")
    private LocalDateTime emailSentAt;

    @OneToMany(
            mappedBy = "invoice",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<InvoiceLine> lines = new ArrayList<>();

    @PrePersist
    public void prePersist() {

        if (invoiceId == null || invoiceId.isBlank()) {
            invoiceId = "invoice-" + UUID.randomUUID();
        }

        if (invoiceDate == null) {
            invoiceDate = LocalDateTime.now();
        }
    }

    public void addLine(InvoiceLine line) {
        lines.add(line);
        line.setInvoice(this);
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getBasketId() {
        return basketId;
    }

    public void setBasketId(String basketId) {
        this.basketId = basketId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getCompanyNif() {
        return companyNif;
    }

    public void setCompanyNif(String companyNif) {
        this.companyNif = companyNif;
    }

    public String getCompanyRccm() {
        return companyRccm;
    }

    public void setCompanyRccm(String companyRccm) {
        this.companyRccm = companyRccm;
    }

    public String getCompanyAggrement() {
        return companyAggrement;
    }

    public void setCompanyAggrement(String companyAggrement) {
        this.companyAggrement = companyAggrement;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public String getBuyerWhatsapp() {
        return buyerWhatsapp;
    }

    public void setBuyerWhatsapp(String buyerWhatsapp) {
        this.buyerWhatsapp = buyerWhatsapp;
    }

    public BigDecimal getBasketAmount() {
        return basketAmount;
    }

    public void setBasketAmount(BigDecimal basketAmount) {
        this.basketAmount = basketAmount;
    }

    public BigDecimal getFeesAmount() {
        return feesAmount;
    }

    public void setFeesAmount(BigDecimal feesAmount) {
        this.feesAmount = feesAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getProviderTransactionId() {
        return providerTransactionId;
    }

    public void setProviderTransactionId(String providerTransactionId) {
        this.providerTransactionId = providerTransactionId;
    }

    public String getPaymentOperator() {
        return paymentOperator;
    }

    public void setPaymentOperator(String paymentOperator) {
        this.paymentOperator = paymentOperator;
    }

    public String getPaymentAccount() {
        return paymentAccount;
    }

    public void setPaymentAccount(String paymentAccount) {
        this.paymentAccount = paymentAccount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public LocalDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public boolean isEmailSent() {
        return emailSent;
    }

    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
    }

    public LocalDateTime getEmailSentAt() {
        return emailSentAt;
    }

    public void setEmailSentAt(LocalDateTime emailSentAt) {
        this.emailSentAt = emailSentAt;
    }

    public List<InvoiceLine> getLines() {
        return lines;
    }

    public void setLines(List<InvoiceLine> lines) {
        this.lines = lines;
    }

}