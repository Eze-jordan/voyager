package com.solutechOne.voyager.repositories;

import com.solutechOne.voyager.model.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItineraryRepository extends JpaRepository<Itinerary, String> {

    boolean existsByDepartureCity_CityIdAndArrivalCity_CityId(String departureCityId, String arrivalCityId);

    Optional<Itinerary> findByDepartureCity_CityIdAndArrivalCity_CityId(String departureCityId, String arrivalCityId);
}