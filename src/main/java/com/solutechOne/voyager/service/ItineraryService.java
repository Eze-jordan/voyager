package com.solutechOne.voyager.service;

import com.solutechOne.voyager.model.City;
import com.solutechOne.voyager.model.Itinerary;
import com.solutechOne.voyager.model.ItineraryStep;
import com.solutechOne.voyager.repositories.CityRepository;
import com.solutechOne.voyager.repositories.ItineraryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ItineraryService {

    private final ItineraryRepository repository;
    private final CityRepository cityRepository;

    public ItineraryService(ItineraryRepository repository, CityRepository cityRepository) {
        this.repository = repository;
        this.cityRepository = cityRepository;
    }

    public Itinerary create(Itinerary itinerary) {
        if (itinerary == null) {
            throw new RuntimeException("Itinerary is required");
        }

        if (itinerary.getDepartureCity() == null || itinerary.getDepartureCity().getCityId() == null) {
            throw new RuntimeException("Departure city is required");
        }

        if (itinerary.getArrivalCity() == null || itinerary.getArrivalCity().getCityId() == null) {
            throw new RuntimeException("Arrival city is required");
        }

        City dep = cityRepository.findById(itinerary.getDepartureCity().getCityId())
                .orElseThrow(() -> new RuntimeException("Departure city not found"));

        City arr = cityRepository.findById(itinerary.getArrivalCity().getCityId())
                .orElseThrow(() -> new RuntimeException("Arrival city not found"));

        if (dep.getCityId().equals(arr.getCityId())) {
            throw new RuntimeException("Departure city and arrival city cannot be the same");
        }

        itinerary.setDepartureCity(dep);
        itinerary.setArrivalCity(arr);

        validateSteps(itinerary);

        return repository.save(itinerary);
    }

    public Itinerary update(String id, Itinerary updated) {
        Itinerary existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Itinerary not found"));

        if (updated == null) {
            throw new RuntimeException("Updated itinerary is required");
        }

        if (updated.getDepartureCity() == null || updated.getDepartureCity().getCityId() == null) {
            throw new RuntimeException("Departure city is required");
        }

        if (updated.getArrivalCity() == null || updated.getArrivalCity().getCityId() == null) {
            throw new RuntimeException("Arrival city is required");
        }

        City dep = cityRepository.findById(updated.getDepartureCity().getCityId())
                .orElseThrow(() -> new RuntimeException("Departure city not found"));

        City arr = cityRepository.findById(updated.getArrivalCity().getCityId())
                .orElseThrow(() -> new RuntimeException("Arrival city not found"));

        if (dep.getCityId().equals(arr.getCityId())) {
            throw new RuntimeException("Departure city and arrival city cannot be the same");
        }

        existing.setDepartureCity(dep);
        existing.setArrivalCity(arr);
        existing.setSteps(updated.getSteps());

        validateSteps(existing);

        return repository.save(existing);
    }

    @Transactional(readOnly = true)
    public Itinerary getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Itinerary not found"));
    }

    @Transactional(readOnly = true)
    public List<Itinerary> getAll() {
        return repository.findAll();
    }

    public void delete(String id) {
        Itinerary itinerary = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Itinerary not found"));
        repository.delete(itinerary);
    }

    private void validateSteps(Itinerary itinerary) {
        if (itinerary.getSteps() == null || itinerary.getSteps().isEmpty()) {
            return;
        }

        Set<Integer> orders = new HashSet<>();
        Set<String> stepCityNames = new HashSet<>();

        for (ItineraryStep step : itinerary.getSteps()) {
            if (step == null) {
                throw new RuntimeException("A step cannot be null");
            }

            if (step.getStepOrder() == null || step.getStepOrder() < 1) {
                throw new RuntimeException("stepOrder must be >= 1");
            }

            if (step.getStepCityName() == null || step.getStepCityName().isBlank()) {
                throw new RuntimeException("stepCityName is required");
            }

            String normalizedStepCityName = step.getStepCityName().trim().toLowerCase();
            String departureName = itinerary.getDepartureCity().getCityName() != null
                    ? itinerary.getDepartureCity().getCityName().trim().toLowerCase()
                    : "";
            String arrivalName = itinerary.getArrivalCity().getCityName() != null
                    ? itinerary.getArrivalCity().getCityName().trim().toLowerCase()
                    : "";

            if (!orders.add(step.getStepOrder())) {
                throw new RuntimeException("Duplicate stepOrder");
            }

            if (!stepCityNames.add(normalizedStepCityName)) {
                throw new RuntimeException("Duplicate intermediate city");
            }

            if (normalizedStepCityName.equals(departureName)
                    || normalizedStepCityName.equals(arrivalName)) {
                throw new RuntimeException("A step city cannot be the same as departure or arrival city");
            }

            step.setStepCityName(step.getStepCityName().trim());
            step.setItinerary(itinerary);
        }
    }
}