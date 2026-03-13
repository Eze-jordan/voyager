package com.solutechOne.voyager.repositories;


import com.solutechOne.voyager.model.Company;
import com.solutechOne.voyager.model.TransportMeans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface TransportMeansRepository extends JpaRepository<TransportMeans, String> {

    List<TransportMeans> findByCompany_CompanyId(String companyId);

    Optional<TransportMeans> findByMeansIdAndCompany_CompanyId(String meansId, String companyId);

    boolean existsByCompany_CompanyIdAndRegisterId(String companyId, String registerId);
}