package com.solutechOne.voyager.repositories;

import com.solutechOne.voyager.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, String> {
    boolean existsByCompany_CompanyIdAndCity_CityIdAndPlaceName(String companyId, String cityId, String placeName);
    List<Place> findByCompany_CompanyId(String companyId);
    List<Place> findByCity_CityId(String cityId);
}