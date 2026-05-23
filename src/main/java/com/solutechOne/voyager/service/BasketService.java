package com.solutechOne.voyager.service;

import com.solutechOne.voyager.enums.BasketStatus;
import com.solutechOne.voyager.model.Basket;
import com.solutechOne.voyager.model.Company;
import com.solutechOne.voyager.model.Reservation;
import com.solutechOne.voyager.repositories.BasketRepository;
import com.solutechOne.voyager.repositories.CompanyRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BasketService {

    private final BasketRepository basketRepository;
    private final CompanyRepository companyRepository;
    private final TicketPriceService ticketPriceService;

    public BasketService(BasketRepository basketRepository,
                         CompanyRepository companyRepository,
                         TicketPriceService ticketPriceService) {
        this.basketRepository = basketRepository;
        this.companyRepository = companyRepository;
        this.ticketPriceService = ticketPriceService;
    }

    public Basket create(String companyId, String buyerPhone, String buyerWhatsapp, String buyerEmail) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found: " + companyId));

        Basket basket = new Basket();
        basket.setCompany(company);
        basket.setBuyerPhone(normalize(buyerPhone));
        basket.setBuyerWhatsapp(normalize(buyerWhatsapp));
        basket.setBuyerEmail(normalize(buyerEmail));
        basket.setNumberOfReservations(0);
        basket.setBasketStatus(BasketStatus.EN_COURS);

        return basketRepository.save(basket);
    }

    public Basket getById(String basketId) {
        return basketRepository.findById(basketId)
                .orElseThrow(() -> new RuntimeException("Basket not found: " + basketId));
    }

    public List<Basket> getByCompany(String companyId) {
        return basketRepository.findByCompany_CompanyId(companyId);
    }

    public Basket updateAmount(String basketId, BigDecimal newAmount) {
        Basket basket = getById(basketId);

        if (basket.getBasketStatus() == BasketStatus.PAYE) {
            throw new IllegalStateException("Cannot update a paid basket");
        }

        if (basket.getBasketStatus() == BasketStatus.PAYMENT_PENDING) {
            throw new IllegalStateException("Cannot update a basket with payment in progress");
        }

        if (newAmount == null || newAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("basketAmount must be >= 0");
        }

        basket.setBasketAmount(newAmount.setScale(2, RoundingMode.HALF_UP));
        recalcFeesAndTotal(basket);

        return basketRepository.save(basket);
    }

    public Basket markPaymentPending(String basketId, String paymentService, String paymentId, String paymentAccount) {
        Basket basket = getById(basketId);

        if (basket.getBasketStatus() == BasketStatus.PAYE) {
            throw new IllegalStateException("Basket is already paid");
        }

        if (basket.getBasketAmount() == null || basket.getBasketAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Cannot initiate payment for basket amount <= 0");
        }

        recalcFeesAndTotal(basket);

        basket.setBasketPaymentService(paymentService);
        basket.setBasketPaymentId(paymentId);
        basket.setBasketPaymentAccount(paymentAccount);
        basket.setBasketPaymentDate(LocalDateTime.now());
        basket.setBasketStatus(BasketStatus.PAYMENT_PENDING);

        return basketRepository.save(basket);
    }

    public Basket markPaidByCallback(String basketId, String paymentService, String paymentId, String paymentAccount) {
        Basket basket = getById(basketId);

        recalcFeesAndTotal(basket);

        basket.setBasketPaymentService(paymentService);
        basket.setBasketPaymentId(paymentId);
        basket.setBasketPaymentAccount(paymentAccount);
        basket.setBasketPaymentDate(LocalDateTime.now());
        basket.setBasketStatus(BasketStatus.PAYE);

        return basketRepository.save(basket);
    }

    public Basket markPaymentFailed(String basketId) {
        Basket basket = getById(basketId);

        if (basket.getBasketStatus() == BasketStatus.PAYE) {
            return basket;
        }

        basket.setBasketStatus(BasketStatus.EN_COURS);
        return basketRepository.save(basket);
    }

    public Basket abandon(String basketId) {
        Basket basket = getById(basketId);

        if (basket.getBasketStatus() == BasketStatus.PAYE) {
            throw new IllegalStateException("Cannot abandon a paid basket");
        }

        if (basket.getBasketStatus() == BasketStatus.PAYMENT_PENDING) {
            throw new IllegalStateException("Cannot abandon a basket with payment in progress");
        }

        basket.setBasketStatus(BasketStatus.ABANDONNE);
        return basketRepository.save(basket);
    }

    public void delete(String basketId) {
        Basket basket = getById(basketId);

        if (basket.getBasketStatus() == BasketStatus.PAYE) {
            throw new IllegalStateException("Cannot delete a paid basket");
        }

        if (basket.getBasketStatus() == BasketStatus.PAYMENT_PENDING) {
            throw new IllegalStateException("Cannot delete a basket with payment in progress");
        }

        basketRepository.delete(basket);
    }

    public void recalcFeesAndTotal(Basket basket) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        if (basket.getReservations() != null) {
            for (Reservation reservation : basket.getReservations()) {
                if (reservation.getTicketPrice() == null) {
                    continue;
                }
                BigDecimal ticketPrice = ticketPriceService
                        .calculateFinalPrice(reservation.getTicketPrice().getPriceId());
                totalAmount = totalAmount.add(ticketPrice);
            }
        }

        basket.setBasketAmount(totalAmount);

        BigDecimal rate = basket.getCompany() != null && basket.getCompany().getRateFees() != null
                ? basket.getCompany().getRateFees()
                : BigDecimal.ZERO;

        BigDecimal fees = totalAmount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = totalAmount.add(fees).setScale(2, RoundingMode.HALF_UP);

        basket.setBasketFees(fees);
        basket.setBasketTotalAmount(total);

        basketRepository.save(basket);
    }

    private String normalize(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}