package com.solutechOne.voyager.service;

import com.solutechOne.voyager.model.Basket;
import com.solutechOne.voyager.enums.BasketStatus;
import com.solutechOne.voyager.model.Company;
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

    public BasketService(BasketRepository basketRepository, CompanyRepository companyRepository) {
        this.basketRepository = basketRepository;
        this.companyRepository = companyRepository;
    }

    // CREATE
    public Basket create(String companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found: " + companyId));

        Basket basket = new Basket();
        basket.setCompany(company);
        // les valeurs default (status + 0) sont déjà dans @PrePersist
        return basketRepository.save(basket);
    }

    // GET BY ID
    public Basket getById(String basketId) {
        return basketRepository.findById(basketId)
                .orElseThrow(() -> new RuntimeException("Basket not found: " + basketId));
    }

    // LIST BY COMPANY
    public List<Basket> getByCompany(String companyId) {
        return basketRepository.findByCompany_CompanyId(companyId);
    }

    // UPDATE AMOUNT
    public Basket updateAmount(String basketId, BigDecimal newAmount) {
        Basket basket = getById(basketId);

        if (basket.getBasketStatus() == BasketStatus.PAYE) {
            throw new IllegalStateException("Cannot update a paid basket");
        }
        if (newAmount == null || newAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("basketAmount must be >= 0");
        }

        basket.setBasketAmount(newAmount.setScale(2, RoundingMode.HALF_UP));
        recalcFeesAndTotal(basket);

        return basketRepository.save(basket);
    }

    // PAY
    public Basket pay(String basketId, String paymentService, String paymentId, String paymentAccount) {
        Basket basket = getById(basketId);

        if (basket.getBasketStatus() == BasketStatus.PAYE) {
            return basket; // idempotent
        }
        if (basket.getBasketAmount() == null || basket.getBasketAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Cannot pay a basket with amount <= 0");
        }

        recalcFeesAndTotal(basket);

        basket.setBasketPaymentService(paymentService);
        basket.setBasketPaymentId(paymentId);
        basket.setBasketPaymentAccount(paymentAccount);
        basket.setBasketPaymentDate(LocalDateTime.now());
        basket.setBasketStatus(BasketStatus.PAYE);

        return basketRepository.save(basket);
    }

    // ABANDON
    public Basket abandon(String basketId) {
        Basket basket = getById(basketId);

        if (basket.getBasketStatus() == BasketStatus.PAYE) {
            throw new IllegalStateException("Cannot abandon a paid basket");
        }

        basket.setBasketStatus(BasketStatus.ABANDONNE);
        return basketRepository.save(basket);
    }

    // DELETE (optionnel)
    public void delete(String basketId) {
        Basket basket = getById(basketId);

        if (basket.getBasketStatus() == BasketStatus.PAYE) {
            throw new IllegalStateException("Cannot delete a paid basket");
        }

        basketRepository.delete(basket);
    }

    private void recalcFeesAndTotal(Basket basket) {
        BigDecimal rate = basket.getCompany() != null ? basket.getCompany().getRateFees() : BigDecimal.ZERO;
        if (rate == null) rate = BigDecimal.ZERO;

        BigDecimal amount = basket.getBasketAmount() == null ? BigDecimal.ZERO : basket.getBasketAmount();

        BigDecimal fees = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = amount.add(fees).setScale(2, RoundingMode.HALF_UP);

        basket.setBasketFees(fees);
        basket.setBasketTotalAmount(total);
    }
}