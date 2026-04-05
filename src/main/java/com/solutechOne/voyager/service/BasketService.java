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
    private final TicketPriceService ticketPriceService;  // Service pour calculer le prix final des billets

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

        return basketRepository.save(basket);
    }

    private String normalize(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
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

        if (newAmount == null || newAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("basketAmount must be >= 0");
        }

        basket.setBasketAmount(newAmount.setScale(2, RoundingMode.HALF_UP));
        recalcFeesAndTotal(basket);

        return basketRepository.save(basket);
    }

    public Basket pay(String basketId, String paymentService, String paymentId, String paymentAccount) {
        Basket basket = getById(basketId);

        if (basket.getBasketStatus() == BasketStatus.PAYE) {
            return basket;
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

    public Basket abandon(String basketId) {
        Basket basket = getById(basketId);

        if (basket.getBasketStatus() == BasketStatus.PAYE) {
            throw new IllegalStateException("Cannot abandon a paid basket");
        }

        basket.setBasketStatus(BasketStatus.ABANDONNE);
        return basketRepository.save(basket);
    }

    public void delete(String basketId) {
        Basket basket = getById(basketId);

        if (basket.getBasketStatus() == BasketStatus.PAYE) {
            throw new IllegalStateException("Cannot delete a paid basket");
        }

        basketRepository.delete(basket);
    }

    // Méthode pour recalculer les frais et le montant total du panier
    public void recalcFeesAndTotal(Basket basket) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        // Calcul du total des réservations avec promo appliquée
        for (Reservation reservation : basket.getReservations()) {
            // Récupère le prix final du billet, tenant compte de la promo
            BigDecimal ticketPrice = ticketPriceService.calculateFinalPrice(reservation.getTicketPrice().getPriceId());  // Utilisation de getPriceId()

            // Ajoute ce prix au total
            totalAmount = totalAmount.add(ticketPrice);
        }

        basket.setBasketAmount(totalAmount);  // Met à jour le montant du panier

        // Calcul des frais selon le taux de la compagnie
        BigDecimal rate = basket.getCompany() != null && basket.getCompany().getRateFees() != null
                ? basket.getCompany().getRateFees()
                : BigDecimal.ZERO;

        BigDecimal fees = totalAmount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = totalAmount.add(fees).setScale(2, RoundingMode.HALF_UP);

        basket.setBasketFees(fees);  // Met à jour les frais
        basket.setBasketTotalAmount(total);  // Met à jour le montant total avec les frais

        basketRepository.save(basket);  // Sauvegarde du panier mis à jour
    }
}