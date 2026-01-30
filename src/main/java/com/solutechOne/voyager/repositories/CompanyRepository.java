package com.solutechOne.voyager.repositories;

import com.solutechOne.voyager.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, String> {

    Optional<Company> findByEmail(String email);
    boolean existsByEmail(String email);
}
