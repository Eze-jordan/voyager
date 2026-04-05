package com.solutechOne.voyager.repositories;

import com.solutechOne.voyager.model.Tpe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TpeRepository extends JpaRepository<Tpe, String> {

    List<Tpe> findByCompanyId(String companyId);
}