package com.solutechOne.voyager.service;

import com.solutechOne.voyager.dto.DepartureCreateRequest;
import com.solutechOne.voyager.model.*;
import com.solutechOne.voyager.repositories.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class DepartureService {

    private final DepartureRepository departureRepository;
    private final CompanyRepository companyRepository;
    private final TransportMeansRepository transportMeansRepository;
    private final CityRepository cityRepository;
    private final PlaceRepository placeRepository;

    public DepartureService(DepartureRepository departureRepository,
                            CompanyRepository companyRepository,
                            TransportMeansRepository transportMeansRepository,
                            CityRepository cityRepository,
                            PlaceRepository placeRepository) {
        this.departureRepository = departureRepository;
        this.companyRepository = companyRepository;
        this.transportMeansRepository = transportMeansRepository;
        this.cityRepository = cityRepository;
        this.placeRepository = placeRepository;
    }
    // Créer un départ via un DTO
    public Departure createDeparture(DepartureCreateRequest req) {
        if (req == null) throw new IllegalArgumentException("Request body is required");

        String companyId = req.companyId;
        String meansId = req.meansId;
        String departureCityId = req.departureCityId;
        String departureBoardingPlaceId = req.departureBoardingPlaceId;

        if (companyId == null || companyId.isBlank())
            throw new RuntimeException("companyId est obligatoire");

        if (meansId == null || meansId.isBlank())
            throw new RuntimeException("meansId est obligatoire");

        if (departureCityId == null || departureCityId.isBlank())
            throw new RuntimeException("departureCityId est obligatoire");

        if (departureBoardingPlaceId == null || departureBoardingPlaceId.isBlank())
            throw new RuntimeException("boardingPlaceId est obligatoire");

        // Vérifier si la référence de départ existe déjà pour la compagnie
        if (departureRepository.existsByCompany_CompanyIdAndDepartureReference(companyId, req.departureReference)) {
            throw new RuntimeException("departureReference déjà utilisée pour cette compagnie");
        }

        // Récupérer la compagnie et les autres informations
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company introuvable"));

        TransportMeans means = transportMeansRepository.findById(meansId)
                .orElseThrow(() -> new RuntimeException("TransportMeans introuvable"));

        City city = cityRepository.findById(departureCityId)
                .orElseThrow(() -> new RuntimeException("City introuvable"));

        Place boardingPlace = placeRepository.findById(departureBoardingPlaceId)
                .orElseThrow(() -> new RuntimeException("Place introuvable"));

        // Créer l'entité Departure
        Departure departure = new Departure();
        departure.setCompany(company);
        departure.setMeans(means);
        departure.setDepartureCity(city);
        departure.setDepartureBoardingPlace(boardingPlace);
        departure.setDepartureReference(req.departureReference);
        departure.setDepartureDate(req.departureDate);
        departure.setDepartureTime(req.departureTime);
        departure.setDepartureCheckinStart(req.departureCheckinStart);
        departure.setDepartureCheckinEnd(req.departureCheckinEnd);
        departure.setDepartureBoardingTime(req.departureBoardingTime);

        // Validation des données
        validateDeparture(departure);

        return departureRepository.save(departure);
    }


    public Departure updateDeparture(String id, Departure updated) {

        return departureRepository.findById(id)
                .map(dep -> {
                    dep.setDepartureReference(updated.getDepartureReference());
                    dep.setDepartureDate(updated.getDepartureDate());
                    dep.setDepartureTime(updated.getDepartureTime());
                    dep.setDepartureCheckinStart(updated.getDepartureCheckinStart());
                    dep.setDepartureCheckinEnd(updated.getDepartureCheckinEnd());
                    dep.setDepartureBoardingTime(updated.getDepartureBoardingTime());
                    dep.setDepartureStatus(updated.getDepartureStatus());

                    validateDeparture(dep);
                    return departureRepository.save(dep);
                })
                .orElseThrow(() -> new RuntimeException("Departure not found"));
    }

    public void deleteDeparture(String id) {
        departureRepository.deleteById(id);
    }

    public List<Departure> getAllDepartures() {
        return departureRepository.findAll();
    }

    public Optional<Departure> getDepartureById(String id) {
        return departureRepository.findById(id);
    }

    public List<Departure> getDeparturesByCompany(String companyId) {
        return departureRepository.findByCompany_CompanyId(companyId);
    }

    private void validateDeparture(Departure departure) {
        if (departure.getDepartureReference() == null || departure.getDepartureReference().isBlank())
            throw new RuntimeException("Departure reference is required");

        if (departure.getCompany() == null)
            throw new RuntimeException("Company is required");

        if (departure.getMeans() == null)
            throw new RuntimeException("Means is required");

        if (departure.getDepartureCity() == null)
            throw new RuntimeException("Departure city is required");

        if (departure.getDepartureBoardingPlace() == null)
            throw new RuntimeException("Boarding place is required");

        if (departure.getDepartureDate() == null)
            throw new RuntimeException("Departure date is required");

        if (departure.getDepartureTime() == null)
            throw new RuntimeException("Departure time is required");

        // Bonus cohérence horaires
        if (departure.getDepartureCheckinStart() != null && departure.getDepartureCheckinEnd() != null) {
            if (departure.getDepartureCheckinEnd().isBefore(departure.getDepartureCheckinStart())) {
                throw new RuntimeException("checkinEnd ne peut pas être avant checkinStart");
            }
        }
        if (departure.getDepartureBoardingTime() != null && departure.getDepartureTime() != null) {
            if (departure.getDepartureBoardingTime().isAfter(departure.getDepartureTime())) {
                throw new RuntimeException("boardingTime ne peut pas être après departureTime");
            }
        }
    }


}