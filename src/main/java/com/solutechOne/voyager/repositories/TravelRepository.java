package com.solutechOne.voyager.repositories;

import com.solutechOne.voyager.model.Travel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelRepository extends JpaRepository<Travel, String> {

    List<Travel> findByBasket_BasketId(String basketId);

    boolean existsByBasket_BasketIdAndDeparture_DepartureIdAndArrival_ArrivalId(
            String basketId,
            String departureId,
            String arrivalId
    );
}