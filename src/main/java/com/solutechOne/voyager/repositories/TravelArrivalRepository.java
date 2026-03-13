package com.solutechOne.voyager.repositories;

import com.solutechOne.voyager.model.TravelArrival;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TravelArrivalRepository extends JpaRepository<TravelArrival, String> {

    List<TravelArrival> findByDeparture_DepartureId(String departureId);
}