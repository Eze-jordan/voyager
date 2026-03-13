package com.solutechOne.voyager.service;

import com.solutechOne.voyager.enums.TicketStatus;
import com.solutechOne.voyager.enums.YesNo;
import com.solutechOne.voyager.model.City;
import com.solutechOne.voyager.model.Company;
import com.solutechOne.voyager.model.TicketPrice;
import com.solutechOne.voyager.model.TravelClass;
import com.solutechOne.voyager.repositories.CityRepository;
import com.solutechOne.voyager.repositories.CompanyRepository;
import com.solutechOne.voyager.repositories.TicketPriceRepository;
import com.solutechOne.voyager.repositories.TravelClassRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TicketPriceService {

    private final TicketPriceRepository repository;
    private final CompanyRepository companyRepository;
    private final TravelClassRepository travelClassRepository;
    private final CityRepository cityRepository;

    public TicketPriceService(TicketPriceRepository repository,
                              CompanyRepository companyRepository,
                              TravelClassRepository travelClassRepository,
                              CityRepository cityRepository) {
        this.repository = repository;
        this.companyRepository = companyRepository;
        this.travelClassRepository = travelClassRepository;
        this.cityRepository = cityRepository;
    }

    public TicketPrice create(TicketPrice tp) {

        // IDs viennent du BODY
        if (tp.getCompanyId() == null || tp.getCompanyId().isBlank())
            throw new RuntimeException("companyId obligatoire");

        if (tp.getClassId() == null || tp.getClassId().isBlank())
            throw new RuntimeException("classId obligatoire");

        if (tp.getDepartureCityId() == null || tp.getDepartureCityId().isBlank())
            throw new RuntimeException("departureCityId obligatoire");

        if (tp.getArrivalCityId() == null || tp.getArrivalCityId().isBlank())
            throw new RuntimeException("arrivalCityId obligatoire");

        // validations simples
        if (tp.getTicketTitle() == null || tp.getTicketTitle().isBlank())
            throw new RuntimeException("ticketTitle obligatoire");

        if (tp.getTicketPrice() == null)
            throw new RuntimeException("ticketPrice obligatoire");

        if (tp.getTicketAgeMini() == null || tp.getTicketAgeMaxi() == null || tp.getTicketAgeMini() > tp.getTicketAgeMaxi())
            throw new RuntimeException("ticketAgeMini/ticketAgeMaxi invalides");

        // unique business
        if (repository.existsByCompany_CompanyIdAndTravelClass_ClassIdAndDepartureCity_CityIdAndArrivalCity_CityIdAndTicketTitle(
                tp.getCompanyId(), tp.getClassId(), tp.getDepartureCityId(), tp.getArrivalCityId(), tp.getTicketTitle())) {
            throw new RuntimeException("Ce tarif existe déjà pour cette route/classe/titre");
        }

        Company company = companyRepository.findById(tp.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company introuvable"));

        TravelClass travelClass = travelClassRepository.findById(tp.getClassId())
                .orElseThrow(() -> new RuntimeException("TravelClass introuvable"));

        City depCity = cityRepository.findById(tp.getDepartureCityId())
                .orElseThrow(() -> new RuntimeException("Departure city introuvable"));

        City arrCity = cityRepository.findById(tp.getArrivalCityId())
                .orElseThrow(() -> new RuntimeException("Arrival city introuvable"));

        tp.setCompany(company);
        tp.setTravelClass(travelClass);
        tp.setDepartureCity(depCity);
        tp.setArrivalCity(arrCity);

        // ⚠️ ordre important : si ticketPromoApply est null, on met NON avant de tester
        if (tp.getTicketPromoApply() == null) tp.setTicketPromoApply(YesNo.NON);

        // promo coherence
        if (tp.getTicketPromoApply() == YesNo.NON) {
            tp.setTicketPromoRate(null);
            tp.setTicketPromoStart(null);
            tp.setTicketPromoEnd(null);
        } else {
            if (tp.getTicketPromoRate() == null || tp.getTicketPromoRate() <= 0 || tp.getTicketPromoRate() > 100)
                throw new RuntimeException("ticketPromoRate invalide");

            if (tp.getTicketPromoStart() == null || tp.getTicketPromoEnd() == null)
                throw new RuntimeException("ticketPromoStart/ticketPromoEnd obligatoires si promo = OUI");

            if (tp.getTicketPromoStart().isAfter(tp.getTicketPromoEnd()))
                throw new RuntimeException("ticketPromoStart ne peut pas être après ticketPromoEnd");
        }

        if (tp.getTicketStatus() == null) tp.setTicketStatus(TicketStatus.ACTIF);

        return repository.save(tp);
    }

    public TicketPrice getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket price not found"));
    }

    public List<TicketPrice> getAll() {
        return repository.findAll();
    }

    public List<TicketPrice> getByCompany(String companyId) {
        return repository.findByCompany_CompanyId(companyId);
    }

    public List<TicketPrice> getByRoute(String departureCityId, String arrivalCityId) {
        return repository.findByDepartureCity_CityIdAndArrivalCity_CityId(departureCityId, arrivalCityId);
    }

    public TicketPrice update(String id, TicketPrice updated) {

        TicketPrice existing = getById(id);

        if (updated.getTicketTitle() != null)
            existing.setTicketTitle(updated.getTicketTitle());

        if (updated.getTicketDescription() != null)
            existing.setTicketDescription(updated.getTicketDescription());

        if (updated.getTicketPrice() != null)
            existing.setTicketPrice(updated.getTicketPrice());

        if (updated.getTicketAgeMini() != null)
            existing.setTicketAgeMini(updated.getTicketAgeMini());

        if (updated.getTicketAgeMaxi() != null)
            existing.setTicketAgeMaxi(updated.getTicketAgeMaxi());

        if (updated.getTicketPromoApply() != null)
            existing.setTicketPromoApply(updated.getTicketPromoApply());

        if (updated.getTicketPromoRate() != null)
            existing.setTicketPromoRate(updated.getTicketPromoRate());

        if (updated.getTicketPromoStart() != null)
            existing.setTicketPromoStart(updated.getTicketPromoStart());

        if (updated.getTicketPromoEnd() != null)
            existing.setTicketPromoEnd(updated.getTicketPromoEnd());

        if (updated.getTicketSeatAuthorized() != null)
            existing.setTicketSeatAuthorized(updated.getTicketSeatAuthorized());

        if (updated.getTicketBagAuthorized() != null)
            existing.setTicketBagAuthorized(updated.getTicketBagAuthorized());

        if (updated.getTicketHeightMaxi() != null)
            existing.setTicketHeightMaxi(updated.getTicketHeightMaxi());

        if (updated.getTicketStatus() != null)
            existing.setTicketStatus(updated.getTicketStatus());

        return repository.save(existing);
    }

    public void delete(String id) {

        TicketPrice price = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket price not found"));

        repository.delete(price);
    }


    // 🔥 Calcul prix final avec promo
    public BigDecimal calculateFinalPrice(String id) {

        TicketPrice price = getById(id);

        if (price.getTicketPrice() == null) {
            throw new RuntimeException("Ticket price is not defined");
        }

        if (price.getTicketPromoApply() == YesNo.OUI &&
                price.getTicketPromoRate() != null &&
                price.getTicketPromoRate() > 0 &&
                price.getTicketPromoRate() <= 100 &&
                price.getTicketPromoStart() != null &&
                price.getTicketPromoEnd() != null &&
                !price.getTicketPromoStart().isAfter(price.getTicketPromoEnd()) &&
                !LocalDate.now().isBefore(price.getTicketPromoStart()) &&
                !LocalDate.now().isAfter(price.getTicketPromoEnd())) {

            BigDecimal discount = price.getTicketPrice()
                    .multiply(BigDecimal.valueOf(price.getTicketPromoRate()))
                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);

            return price.getTicketPrice()
                    .subtract(discount)
                    .setScale(2, java.math.RoundingMode.HALF_UP);
        }

        return price.getTicketPrice()
                .setScale(2, java.math.RoundingMode.HALF_UP);
    }
}