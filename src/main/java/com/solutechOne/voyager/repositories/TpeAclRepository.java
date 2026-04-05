package com.solutechOne.voyager.repositories;

import com.solutechOne.voyager.model.TpeAcl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TpeAclRepository extends JpaRepository<TpeAcl, String> {

    List<TpeAcl> findByCompany_CompanyId(String companyId);

    List<TpeAcl> findByTpe_TpeId(String tpeId);

    List<TpeAcl> findByPlaceId(String placeId);
}