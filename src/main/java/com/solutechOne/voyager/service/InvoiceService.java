package com.solutechOne.voyager.service;

import com.solutechOne.voyager.model.*;
import com.solutechOne.voyager.repositories.InvoiceRepository;
import com.solutechOne.voyager.repositories.ReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.solutechOne.voyager.event.InvoiceCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ReservationRepository reservationRepository;
    private final ApplicationEventPublisher eventPublisher;

    public InvoiceService(
            InvoiceRepository invoiceRepository,
            ReservationRepository reservationRepository, ApplicationEventPublisher eventPublisher
    ) {
        this.invoiceRepository = invoiceRepository;
        this.reservationRepository = reservationRepository;
        this.eventPublisher = eventPublisher;
    }

    // =========================================================
    // CRÉATION IDEMPOTENTE DE LA FACTURE
    // =========================================================

    @Transactional
    public Invoice createInvoiceIfAbsent(
            PaymentTransaction payment
    ) {

        if (payment == null) {
            throw new IllegalArgumentException(
                    "Payment transaction is required"
            );
        }

        if (payment.getPaymentId() == null
                || payment.getPaymentId().isBlank()) {

            throw new IllegalArgumentException(
                    "Payment ID is required"
            );
        }

        /*
         * Première protection d'idempotence.
         *
         * Si la facture existe déjà pour ce paiement,
         * on la retourne sans en créer une nouvelle.
         */
        Invoice existingInvoice =
                invoiceRepository
                        .findByPaymentId(payment.getPaymentId())
                        .orElse(null);

        if (existingInvoice != null) {
            return existingInvoice;
        }

        Basket basket = payment.getBasket();

        if (basket == null) {
            throw new IllegalStateException(
                    "Payment transaction is not linked to a basket"
            );
        }

        Company company = basket.getCompany();

        if (company == null) {
            throw new IllegalStateException(
                    "Basket is not linked to a company"
            );
        }

        // =====================================================
        // FACTURE
        // =====================================================

        Invoice invoice = new Invoice();

        invoice.setInvoiceNumber(
                generateInvoiceNumber()
        );

        // =====================================================
        // IDENTIFIANTS D'ORIGINE
        // =====================================================

        invoice.setPaymentId(
                payment.getPaymentId()
        );

        invoice.setBasketId(
                basket.getBasketId()
        );

        invoice.setCompanyId(
                company.getCompanyId()
        );

        // =====================================================
        // SNAPSHOT COMPANY
        // =====================================================

        invoice.setCompanyName(
                company.getName()
        );

        invoice.setCompanyAddress(
                company.getAddress()
        );

        invoice.setCompanyEmail(
                company.getEmail()
        );

        invoice.setCompanyPhone(
                company.getPhone()
        );

        invoice.setCompanyNif(
                company.getNif()
        );

        invoice.setCompanyRccm(
                company.getRccm()
        );

        invoice.setCompanyAggrement(
                company.getAggrement()
        );

        invoice.setCompanyLogo(
                company.getUrlLogo()
        );

        // =====================================================
        // SNAPSHOT ACHETEUR
        // =====================================================

        invoice.setBuyerEmail(
                basket.getBuyerEmail()
        );

        invoice.setBuyerPhone(
                basket.getBuyerPhone()
        );

        invoice.setBuyerWhatsapp(
                basket.getBuyerWhatsapp()
        );

        // =====================================================
        // MONTANTS
        // =====================================================

        invoice.setBasketAmount(
                safeAmount(
                        basket.getBasketAmount()
                )
        );

        invoice.setFeesAmount(
                safeAmount(
                        basket.getBasketFees()
                )
        );

        /*
         * IMPORTANT :
         *
         * Le montant total de la facture vient du paiement
         * réellement initié et non d'un nouveau calcul.
         */
        invoice.setTotalAmount(
                payment.getAmount() != null
                        ? payment.getAmount()
                        : safeAmount(
                        basket.getBasketTotalAmount()
                )
        );

        // =====================================================
        // PAIEMENT
        // =====================================================

        invoice.setPaymentReference(
                payment.getReference()
        );

        invoice.setProviderTransactionId(
                payment.getExternalTransactionId()
        );

        invoice.setPaymentOperator(
                payment.getOperatorName()
        );

        invoice.setPaymentAccount(
                payment.getCustomerAccountNumber()
        );

        invoice.setPaymentDate(
                basket.getBasketPaymentDate()
                        != null
                        ? basket.getBasketPaymentDate()
                        : LocalDateTime.now()
        );

        invoice.setInvoiceDate(
                LocalDateTime.now()
        );

        invoice.setEmailSent(false);

        // =====================================================
        // LIGNES DE FACTURE
        // =====================================================

        List<Reservation> reservations =
                reservationRepository
                        .findByBasket_BasketId(
                                basket.getBasketId()
                        );

        for (Reservation reservation : reservations) {

            InvoiceLine line =
                    createInvoiceLine(
                            reservation
                    );

            invoice.addLine(line);
        }

        // =====================================================
        // SAUVEGARDE
        // =====================================================

        Invoice savedInvoice =
                invoiceRepository.saveAndFlush(invoice);

        System.out.println(
                ">>> Publication InvoiceCreatedEvent : "
                        + savedInvoice.getInvoiceId()
        );

        eventPublisher.publishEvent(
                new InvoiceCreatedEvent(
                        savedInvoice.getInvoiceId()
                )
        );

        return savedInvoice;
    }

    // =========================================================
    // CONSTRUCTION D'UNE LIGNE
    // =========================================================

    private InvoiceLine createInvoiceLine(
            Reservation reservation
    ) {

        InvoiceLine line =
                new InvoiceLine();

        // =====================================================
        // RÉSERVATION
        // =====================================================

        line.setReservationId(
                reservation.getReservationId()
        );

        line.setReservationReference(
                reservation.getReservationReference()
        );

        // =====================================================
        // PASSAGER
        // =====================================================

        line.setPassengerName(
                reservation.getPassengerName()
        );

        line.setPassengerFirstname(
                reservation.getPassengerFirstname()
        );

        // =====================================================
        // BILLET
        // =====================================================

        TicketPrice ticket =
                reservation.getTicketPrice();

        if (ticket != null) {

            line.setTicketId(
                    ticket.getPriceId()
            );

            line.setTicketTitle(
                    ticket.getTicketTitle()
            );

            line.setTicketDescription(
                    ticket.getTicketDescription()
            );

            line.setUnitPrice(
                    safeAmount(
                            ticket.getTicketPrice()
                    )
            );

            TravelClass travelClass =
                    ticket.getTravelClass();

            if (travelClass != null) {

                line.setTravelClass(
                        travelClass.getClassDesignation()
                );
            }
        } else {

            line.setUnitPrice(
                    BigDecimal.ZERO
            );
        }

        // =====================================================
        // TRAJET
        // =====================================================

        Travel travel =
                reservation.getTravel();

        if (travel != null) {

            Departure departure =
                    travel.getDeparture();

            TravelArrival arrival =
                    travel.getArrival();

            // -------------------------------------------------
            // DÉPART
            // -------------------------------------------------

            if (departure != null) {

                line.setDepartureReference(
                        departure.getDepartureReference()
                );

                line.setDepartureDate(
                        departure.getDepartureDate()
                );

                line.setDepartureTime(
                        departure.getDepartureTime()
                );

                City departureCity =
                        departure.getDepartureCity();

                if (departureCity != null) {

                    line.setDepartureCity(
                            departureCity.getCityName()
                    );
                }
            }

            // -------------------------------------------------
            // ARRIVÉE
            // -------------------------------------------------

            if (arrival != null) {

                line.setArrivalDate(
                        arrival.getArrivalDate()
                );

                line.setArrivalTime(
                        arrival.getArrivalTime()
                );

                City arrivalCity =
                        arrival.getArrivalCity();

                if (arrivalCity != null) {

                    line.setArrivalCity(
                            arrivalCity.getCityName()
                    );
                }
            }
        }

        return line;
    }

    // =========================================================
    // GÉNÉRATION NUMÉRO FACTURE
    // =========================================================

    private String generateInvoiceNumber() {

        String date =
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter
                                        .ofPattern("yyyyMMdd")
                        );

        String random =
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 8)
                        .toUpperCase();

        return "FAC-"
                + date
                + "-"
                + random;
    }

    // =========================================================
    // BIGDECIMAL NULL SAFE
    // =========================================================

    private BigDecimal safeAmount(
            BigDecimal amount
    ) {

        return amount != null
                ? amount
                : BigDecimal.ZERO;
    }
}