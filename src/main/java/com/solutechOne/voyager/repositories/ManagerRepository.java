package com.solutechOne.voyager.repositories;


import com.solutechOne.voyager.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, String> {
    Optional<Manager> findByEmail(String email);
    boolean existsByEmail(String email);


    boolean existsByNumeroTelephoneManager(String numeroTelephoneManager);
}
