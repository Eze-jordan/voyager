package com.solutechOne.voyager.service;

import com.solutechOne.voyager.dto.TravelWithTicketsResponse;
import com.solutechOne.voyager.enums.BasketStatus;
import com.solutechOne.voyager.model.Basket;
import com.solutechOne.voyager.model.Departure;
import com.solutechOne.voyager.model.TicketPrice;
import com.solutechOne.voyager.model.Travel;
import com.solutechOne.voyager.model.TravelArrival;
import com.solutechOne.voyager.repositories.BasketRepository;
import com.solutechOne.voyager.repositories.DepartureRepository;
import com.solutechOne.voyager.repositories.TicketPriceRepository;
import com.solutechOne.voyager.repositories.TravelArrivalRepository;
import com.solutechOne.voyager.repositories.TravelRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class TravelService {

    private final TravelRepository travelRepository;
    private final BasketRepository basketRepository;
    private final DepartureRepository departureRepository;
    private final TravelArrivalRepository arrivalRepository;
    private final TicketPriceRepository ticketPriceRepository;

    public TravelService(
            TravelRepository travelRepository,
            BasketRepository basketRepository,
            DepartureRepository departureRepository,
            TravelArrivalRepository arrivalRepository,
            TicketPriceRepository ticketPriceRepository
    ) {
        this.travelRepository = travelRepository;
        this.basketRepository = basketRepository;
        this.departureRepository = departureRepository;
        this.arrivalRepository = arrivalRepository;
        this.ticketPriceRepository = ticketPriceRepository;
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

    public List<TicketPrice> getRelatedTicketsByTravel(String travelId) {
        Travel travel = getById(travelId);

        if (travel.getDeparture() == null || travel.getArrival() == null) {
            throw new IllegalStateException("Travel must have departure and arrival");
        }

        if (travel.getDeparture().getDepartureCity() == null) {
            throw new IllegalStateException("Departure city is missing");
        }

        if (travel.getArrival().getArrivalCity() == null) {
            throw new IllegalStateException("Arrival city is missing");
        }

        String departureCityId = travel.getDeparture()
                .getDepartureCity()
                .getCityId();

        String arrivalCityId = travel.getArrival()
                .getArrivalCity()
                .getCityId();

        return ticketPriceRepository.findByDepartureCity_CityIdOrArrivalCity_CityId(
                departureCityId,
                arrivalCityId
        );
    }

    public TravelWithTicketsResponse createAndReturnTickets(
            String basketId,
            String departureId,
            String arrivalId,
            LocalDate date
    ) {
        // Vérification de la date avant de créer le Travel
        Departure departure = departureRepository.findById(departureId)
                .orElseThrow(() ->
                        new RuntimeException("Departure not found: " + departureId)
                );

        if (date == null) {
            throw new IllegalArgumentException("La date est obligatoire");
        }

        if (!departure.getDepartureDate().equals(date)) {
            throw new IllegalStateException(
                    "Le départ ne correspond pas à la date demandée"
            );
        }

        // La date est correcte : on peut maintenant créer le Travel
        Travel travel = create(basketId, departureId, arrivalId);

        List<TicketPrice> tickets =
                getRelatedTicketsByTravel(travel.getTravelId());

        List<TravelWithTicketsResponse.TicketItemResponse> ticketItems =
                tickets.stream()
                        .map(ticket -> new TravelWithTicketsResponse.TicketItemResponse(
                                ticket.getPriceId(),
                                ticket.getTicketTitle(),
                                ticket.getTicketPrice(),
                                ticket.getTravelClass() != null
                                        ? ticket.getTravelClass().getClassId()
                                        : null,
                                ticket.getTravelClass() != null
                                        ? ticket.getTravelClass().getClassDesignation()
                                        : null
                        ))
                        .toList();

        return new TravelWithTicketsResponse(
                travel.getTravelId(),
                travel.getBasket().getBasketId(),

                // Départ
                travel.getDeparture().getDepartureId(),
                travel.getDeparture().getDepartureDate(),
                travel.getDeparture().getDepartureTime(),

                // Arrivée
                travel.getArrival().getArrivalId(),
                travel.getArrival().getArrivalDate(),
                travel.getArrival().getArrivalTime(),

                ticketItems
        );
    }
}