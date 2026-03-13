package com.solutechOne.voyager.repositories;

import com.solutechOne.voyager.model.TravelClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelClassRepository extends JpaRepository<TravelClass, String> {

    List<TravelClass> findByCompany_CompanyId(String companyId);

    boolean existsByCompany_CompanyIdAndClassDesignation(String companyId, String classDesignation);
}