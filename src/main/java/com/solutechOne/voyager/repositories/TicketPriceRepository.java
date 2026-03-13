package com.solutechOne.voyager.repositories;


import com.solutechOne.voyager.enums.TicketStatus;
import com.solutechOne.voyager.model.TicketPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketPriceRepository extends JpaRepository<TicketPrice, String> {

    List<TicketPrice> findByCompany_CompanyId(String companyId);

    List<TicketPrice> findByDepartureCity_CityIdAndArrivalCity_CityId(String departureCityId, String arrivalCityId);

    List<TicketPrice> findByTicketStatus(TicketStatus ticketStatus);

    boolean existsByCompany_CompanyIdAndTravelClass_ClassIdAndDepartureCity_CityIdAndArrivalCity_CityIdAndTicketTitle(
            String companyId, String classId, String depCityId, String arrCityId, String ticketTitle
    );
}