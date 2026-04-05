package com.solutechOne.voyager.service;

import com.solutechOne.voyager.enums.BasketStatus;
import com.solutechOne.voyager.model.Basket;
import com.solutechOne.voyager.model.Departure;
import com.solutechOne.voyager.model.Travel;
import com.solutechOne.voyager.model.TravelArrival;
import com.solutechOne.voyager.repositories.BasketRepository;
import com.solutechOne.voyager.repositories.DepartureRepository;
import com.solutechOne.voyager.repositories.TravelArrivalRepository;
import com.solutechOne.voyager.repositories.TravelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TravelService {

    private final TravelRepository travelRepository;
    private final BasketRepository basketRepository;
    private final DepartureRepository departureRepository;
    private final TravelArrivalRepository arrivalRepository;

    public TravelService(
            TravelRepository travelRepository,
            BasketRepository basketRepository,
            DepartureRepository departureRepository,
            TravelArrivalRepository arrivalRepository
    ) {
        this.travelRepository = travelRepository;
        this.basketRepository = basketRepository;
        this.departureRepository = departureRepository;
        this.arrivalRepository = arrivalRepository;
    }

    public Travel create(String basketId, String departureId, String arrivalId) {

        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new RuntimeException("Basket not found: " + basketId));

        if (basket.getBasketStatus() == BasketStatus.PAYE) {
            throw new IllegalStateException("Cannot add a travel to a paid basket");
        }

        Departure departure = departureRepository.findById(departureId)
                .orElseThrow(() -> new RuntimeException("Departure not found: " + departureId));

        TravelArrival arrival = arrivalRepository.findById(arrivalId)
                .orElseThrow(() -> new RuntimeException("Arrival not found: " + arrivalId));

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

        return travelRepository.save(travel);
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
    }
}