package com.solutechOne.voyager.service;

import com.solutechOne.voyager.dto.CityCreateRequest;
import com.solutechOne.voyager.model.City;
import com.solutechOne.voyager.model.Company;
import com.solutechOne.voyager.repositories.CityRepository;
import com.solutechOne.voyager.repositories.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CityService {

    private final CityRepository cityRepository;
    private final CompanyRepository companyRepository;

    public CityService(CityRepository cityRepository, CompanyRepository companyRepository) {
        this.cityRepository = cityRepository;
        this.companyRepository = companyRepository;
    }

    private String generateCityId() {
        return "city-" + UUID.randomUUID();
    }

    // ✅ CREATE via DTO
    public City createCity(CityCreateRequest req) {

        if (req == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (req.companyId == null || req.companyId.isBlank()) {
            throw new IllegalArgumentException("companyId is required");
        }
        if (req.cityName == null || req.cityName.isBlank()) {
            throw new IllegalArgumentException("cityName is required");
        }

        Company company = companyRepository.findById(req.companyId)
                .orElseThrow(() -> new RuntimeException("Company not found: " + req.companyId));

        cityRepository.findByCompany_CompanyIdAndCityName(company.getCompanyId(), req.cityName)
                .ifPresent(existing -> {
                    throw new RuntimeException("City already exists for this company");
                });

        City city = new City();
        city.setCityId(generateCityId());
        city.setCompany(company);
        city.setCityName(req.cityName);

        return cityRepository.save(city);
    }

    public Optional<City> getCityById(String cityId) {
        return cityRepository.findById(cityId);
    }

    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    public List<City> getCitiesByCompanyId(String companyId) {
        return cityRepository.findByCompany_CompanyId(companyId);
    }

    // UPDATE (tu peux garder comme ça pour l’instant)
    public City updateCity(String cityId, City updatedCity) {
        return cityRepository.findById(cityId)
                .map(city -> {
                    if (updatedCity.getCityName() != null && !updatedCity.getCityName().isBlank()) {
                        city.setCityName(updatedCity.getCityName());
                    }
                    // ⚠️ je te conseille de NE PAS changer company ici via body
                    return cityRepository.save(city);
                })
                .orElseThrow(() -> new RuntimeException("City not found with id " + cityId));
    }

    public void deleteCity(String cityId) {
        cityRepository.deleteById(cityId);
    }
}