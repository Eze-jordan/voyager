package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.dto.CityCreateRequest;
import com.solutechOne.voyager.model.City;
import com.solutechOne.voyager.service.CityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/V1/cities")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @PostMapping
    public ResponseEntity<City> createCity(@RequestBody CityCreateRequest req) {
        City createdCity = cityService.createCity(req);
        return new ResponseEntity<>(createdCity, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<City>> getAllCities() {
        return new ResponseEntity<>(cityService.getAllCities(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<City> getCityById(@PathVariable("id") String cityId) {
        return cityService.getCityById(cityId)
                .map(city -> new ResponseEntity<>(city, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<City>> getCitiesByCompany(@PathVariable("companyId") String companyId) {
        List<City> cities = cityService.getCitiesByCompanyId(companyId);
        return new ResponseEntity<>(cities, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<City> updateCity(@PathVariable("id") String cityId, @RequestBody City updatedCity) {
        try {
            City city = cityService.updateCity(cityId, updatedCity);
            return new ResponseEntity<>(city, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable("id") String cityId) {
        cityService.deleteCity(cityId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}