package com.solutechOne.voyager.service;

import com.solutechOne.voyager.dto.PlaceCreateRequest;
import com.solutechOne.voyager.model.City;
import com.solutechOne.voyager.model.Company;
import com.solutechOne.voyager.model.Place;
import com.solutechOne.voyager.repositories.CityRepository;
import com.solutechOne.voyager.repositories.CompanyRepository;
import com.solutechOne.voyager.repositories.PlaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final CompanyRepository companyRepository;
    private final CityRepository cityRepository;

    public PlaceService(PlaceRepository placeRepository,
                        CompanyRepository companyRepository,
                        CityRepository cityRepository) {
        this.placeRepository = placeRepository;
        this.companyRepository = companyRepository;
        this.cityRepository = cityRepository;
    }

    private String generatePlaceId() {
        return "place-" + UUID.randomUUID();
    }

    // ✅ CREATE via DTO
    public Place createPlace(PlaceCreateRequest req) {

        if (req == null) throw new IllegalArgumentException("Request body is required");
        if (req.companyId == null || req.companyId.isBlank()) throw new IllegalArgumentException("companyId is required");
        if (req.cityId == null || req.cityId.isBlank()) throw new IllegalArgumentException("cityId is required");
        if (req.placeName == null || req.placeName.isBlank()) throw new IllegalArgumentException("placeName is required");

        Company company = companyRepository.findById(req.companyId)
                .orElseThrow(() -> new RuntimeException("Company not found: " + req.companyId));

        City city = cityRepository.findById(req.cityId)
                .orElseThrow(() -> new RuntimeException("City not found: " + req.cityId));

        // (optionnel) Vérifier cohérence: city appartient à la même company
        if (city.getCompany() == null || city.getCompany().getCompanyId() == null
                || !city.getCompany().getCompanyId().equals(company.getCompanyId())) {
            throw new IllegalArgumentException("City does not belong to this company");
        }

        if (placeRepository.existsByCompany_CompanyIdAndCity_CityIdAndPlaceName(req.companyId, req.cityId, req.placeName)) {
            throw new RuntimeException("Place already exists for this city and company");
        }

        Place place = new Place();
        place.setPlaceId(generatePlaceId());
        place.setCompany(company);
        place.setCity(city);
        place.setPlaceName(req.placeName);
        place.setPlaceAddress(req.placeAddress);
        place.setPlaceGpsLongitude(req.placeGpsLongitude);
        place.setPlaceGpsLatitude(req.placeGpsLatitude);

        if (req.placeStatus != null && !req.placeStatus.isBlank()) {
            place.setPlaceStatus(Place.PlaceStatus.valueOf(req.placeStatus.toUpperCase()));
        } else {
            place.setPlaceStatus(Place.PlaceStatus.ACTIF);
        }

        return placeRepository.save(place);
    }

    public Optional<Place> getPlaceById(String placeId) {
        return placeRepository.findById(placeId);
    }

    public List<Place> getAllPlaces() {
        return placeRepository.findAll();
    }

    public List<Place> getPlacesByCompanyId(String companyId) {
        return placeRepository.findByCompany_CompanyId(companyId);
    }

    public List<Place> getPlacesByCityId(String cityId) {
        return placeRepository.findByCity_CityId(cityId);
    }

    // UPDATE/DELETE : tu peux garder tes méthodes actuelles si elles compilent
    public void deletePlace(String placeId) {
        placeRepository.deleteById(placeId);
    }
}