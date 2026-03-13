package com.solutechOne.voyager.repositories;

import com.solutechOne.voyager.enums.BasketStatus;
import com.solutechOne.voyager.model.Basket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BasketRepository extends JpaRepository<Basket, String> {

    List<Basket> findByCompany_CompanyId(String companyId);

    List<Basket> findByCompany_CompanyIdAndBasketStatus(String companyId, BasketStatus status);
}