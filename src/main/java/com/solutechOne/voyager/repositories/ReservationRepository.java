package com.solutechOne.voyager.repositories;

import com.solutechOne.voyager.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, String> {

    // Requête pour récupérer une réservation par sa référence
    Optional<Reservation> findByReservationReference(String reservationReference);


    List<Reservation> findByTravel_Basket_BasketId(String basketId);
}