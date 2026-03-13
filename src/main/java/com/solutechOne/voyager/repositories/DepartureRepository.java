package com.solutechOne.voyager.repositories;

import com.solutechOne.voyager.model.Departure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DepartureRepository extends JpaRepository<Departure, String> {

    List<Departure> findByCompany_CompanyId(String companyId);

    List<Departure> findByMeans_MeansId(String meansId);

    List<Departure> findByDepartureCity_CityId(String departureCityId);

    List<Departure> findByDepartureDate(LocalDate date);

    List<Departure> findByDepartureStatus(Departure.DepartureStatus status);

    boolean existsByCompany_CompanyIdAndDepartureReference(String companyId, String departureReference);
}