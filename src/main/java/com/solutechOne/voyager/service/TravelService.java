package com.solutechOne.voyager.service;

import com.solutechOne.voyager.enums.BasketStatus;
import com.solutechOne.voyager.model.*;
import com.solutechOne.voyager.repositories.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TravelService {

    private final TravelRepository travelRepository;
    private final BasketRepository basketRepository;
    private final DepartureRepository departureRepository;
    private final TravelArrivalRepository arrivalRepository;
    private final TicketPriceRepository ticketPriceRepository;
    private final BasketService basketService;
    public TravelService(
            TravelRepository travelRepository,
            BasketRepository basketRepository,
            DepartureRepository departureRepository,
            TravelArrivalRepository arrivalRepository, TicketPriceRepository ticketPriceRepository, BasketService basketService
    ) {
        this.travelRepository = travelRepository;
        this.basketRepository = basketRepository;
        this.departureRepository = departureRepository;
        this.arrivalRepository = arrivalRepository;
        this.ticketPriceRepository = ticketPriceRepository;
        this.basketService = basketService;
    }

    // CREATE
    public Travel create(String basketId, String departureId, String arrivalId, String ticketPriceId) {

        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new RuntimeException("Basket not found: " + basketId));

        if (basket.getBasketStatus() == BasketStatus.PAYE) {
            throw new IllegalStateException("Cannot add a travel to a paid basket");
        }

        Departure departure = departureRepository.findById(departureId)
                .orElseThrow(() -> new RuntimeException("Departure not found: " + departureId));

        TravelArrival arrival = arrivalRepository.findById(arrivalId)
                .orElseThrow(() -> new RuntimeException("Arrival not found: " + arrivalId));

        TicketPrice ticketPrice = ticketPriceRepository.findById(ticketPriceId)
                .orElseThrow(() -> new RuntimeException("TicketPrice not found: " + ticketPriceId));

        boolean exists = travelRepository.existsByBasket_BasketIdAndDeparture_DepartureIdAndArrival_ArrivalId(
                basketId, departureId, arrivalId
        );
        if (exists) {
            throw new IllegalStateException("This travel already exists in this basket");
        }

        Travel travel = new Travel();
        travel.setBasket(basket);
        travel.setDeparture(departure);
        travel.setArrival(arrival);
        travel.setTicketPrice(ticketPrice);
        travel.setTravelAmount(ticketPrice.getTicketPrice()); // adapte le nom du getter si besoin

        Travel saved = travelRepository.save(travel);

        recalcBasketFromTravels(basket);

        return saved;
    }

    public Travel getById(String travelId) {
        return travelRepository.findById(travelId)
                .orElseThrow(() -> new RuntimeException("Travel not found: " + travelId));
    }

    public List<Travel> getByBasket(String basketId) {
        return travelRepository.findByBasket_BasketId(basketId);
    }

    public void delete(String travelId) {
        Travel travel = getById(travelId);

        Basket basket = travel.getBasket();
        if (basket != null && basket.getBasketStatus() == BasketStatus.PAYE) {
            throw new IllegalStateException("Cannot delete travel from a paid basket");
        }

        travelRepository.delete(travel);

        if (basket != null) {
            recalcBasketFromTravels(basket);
        }
    }

    private void recalcBasketFromTravels(Basket basket) {
        List<Travel> travels = travelRepository.findByBasket_BasketId(basket.getBasketId());

        java.math.BigDecimal amount = travels.stream()
                .map(Travel::getTravelAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        basket.setBasketAmount(amount);

        java.math.BigDecimal rate = basket.getCompany() != null
                ? basket.getCompany().getRateFees()
                : java.math.BigDecimal.ZERO;

        if (rate == null) {
            rate = java.math.BigDecimal.ZERO;
        }

        java.math.BigDecimal fees = amount.multiply(rate).setScale(2, java.math.RoundingMode.HALF_UP);
        java.math.BigDecimal total = amount.add(fees).setScale(2, java.math.RoundingMode.HALF_UP);

        basket.setBasketFees(fees);
        basket.setBasketTotalAmount(total);

        basketRepository.save(basket);
    }
}