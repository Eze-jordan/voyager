package com.solutechOne.voyager.repositories;

import com.solutechOne.voyager.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, String> {

    Optional<City> findByCompany_CompanyIdAndCityName(String companyId, String cityName);

    List<City> findByCompany_CompanyId(String companyId);
}