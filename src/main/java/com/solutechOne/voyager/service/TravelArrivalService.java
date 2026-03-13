package com.solutechOne.voyager.service;


import com.solutechOne.voyager.enums.ArrivalStatus;
import com.solutechOne.voyager.model.City;
import com.solutechOne.voyager.model.Departure;
import com.solutechOne.voyager.model.Place;
import com.solutechOne.voyager.model.TravelArrival;
import com.solutechOne.voyager.repositories.CityRepository;
import com.solutechOne.voyager.repositories.DepartureRepository;
import com.solutechOne.voyager.repositories.PlaceRepository;
import com.solutechOne.voyager.repositories.TravelArrivalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TravelArrivalService {

    private final TravelArrivalRepository repository;
    private final DepartureRepository departureRepository;
    private final CityRepository cityRepository;
    private final PlaceRepository placeRepository;

    public TravelArrivalService(TravelArrivalRepository repository,
                                DepartureRepository departureRepository,
                                CityRepository cityRepository,
                                PlaceRepository placeRepository) {
        this.repository = repository;
        this.departureRepository = departureRepository;
        this.cityRepository = cityRepository;
        this.placeRepository = placeRepository;
    }

    public TravelArrival create(TravelArrival arrival) {

        // IDs viennent du body
        if (arrival.getDepartureId() == null || arrival.getDepartureId().isBlank())
            throw new RuntimeException("departureId est obligatoire");

        if (arrival.getArrivalCityId() == null || arrival.getArrivalCityId().isBlank())
            throw new RuntimeException("arrivalCityId est obligatoire");

        if (arrival.getArrivalPlaceId() == null || arrival.getArrivalPlaceId().isBlank())
            throw new RuntimeException("arrivalPlaceId est obligatoire");

        if (arrival.getArrivalDate() == null)
            throw new RuntimeException("arrivalDate est obligatoire");

        if (arrival.getArrivalTime() == null)
            throw new RuntimeException("arrivalTime est obligatoire");

        Departure dep = departureRepository.findById(arrival.getDepartureId())
                .orElseThrow(() -> new RuntimeException("Departure introuvable"));

        City city = cityRepository.findById(arrival.getArrivalCityId())
                .orElseThrow(() -> new RuntimeException("City introuvable"));

        Place place = placeRepository.findById(arrival.getArrivalPlaceId())
                .orElseThrow(() -> new RuntimeException("Place introuvable"));

        arrival.setDeparture(dep);
        arrival.setArrivalCity(city);
        arrival.setArrivalPlace(place);

        if (arrival.getArrivalStatus() == null) {
            arrival.setArrivalStatus(ArrivalStatus.CONFIRME); // adapte si besoin
        }

        return repository.save(arrival);
    }

    public TravelArrival getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Arrival not found"));
    }

    public List<TravelArrival> getAll() {
        return repository.findAll();
    }

    public List<TravelArrival> getByDeparture(String departureId) {
        return repository.findByDeparture_DepartureId(departureId);
    }

    public TravelArrival update(String id, TravelArrival arrival) {

        TravelArrival existing = getById(id);

        // Tu peux autoriser le changement des relations si tu veux.
        // Moi je le permets uniquement si les IDs sont fournis.
        if (arrival.getArrivalCityId() != null && !arrival.getArrivalCityId().isBlank()) {
            City city = cityRepository.findById(arrival.getArrivalCityId())
                    .orElseThrow(() -> new RuntimeException("City introuvable"));
            existing.setArrivalCity(city);
        }

        if (arrival.getArrivalPlaceId() != null && !arrival.getArrivalPlaceId().isBlank()) {
            Place place = placeRepository.findById(arrival.getArrivalPlaceId())
                    .orElseThrow(() -> new RuntimeException("Place introuvable"));
            existing.setArrivalPlace(place);
        }

        if (arrival.getArrivalDate() != null) existing.setArrivalDate(arrival.getArrivalDate());
        if (arrival.getArrivalTime() != null) existing.setArrivalTime(arrival.getArrivalTime());
        if (arrival.getArrivalStatus() != null) existing.setArrivalStatus(arrival.getArrivalStatus());

        return repository.save(existing);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}