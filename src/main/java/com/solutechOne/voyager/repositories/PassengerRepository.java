package com.solutechOne.voyager.repositories;

import com.solutechOne.voyager.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PassengerRepository extends JpaRepository<Passenger, String> {

    // Recherche d'un passager par son email
    Optional<Passenger> findByEmail(String email);
}