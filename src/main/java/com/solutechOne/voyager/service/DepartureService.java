package com.solutechOne.voyager.service;

import com.solutechOne.voyager.dto.DepartureCreateRequest;
import com.solutechOne.voyager.enums.BoardingConfirmed;
import com.solutechOne.voyager.enums.SeatReservationStatus;
import com.solutechOne.voyager.model.*;
import com.solutechOne.voyager.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartureService {

    private final DepartureRepository departureRepository;
    private final CompanyRepository companyRepository;
    private final TransportMeansRepository transportMeansRepository;
    private final CityRepository cityRepository;
    private final PlaceRepository placeRepository;
    private final SeatRepository seatRepository;
    private final SeatReservedRepository seatReservedRepository;

    public DepartureService(
            DepartureRepository departureRepository,
            CompanyRepository companyRepository,
            TransportMeansRepository transportMeansRepository,
            CityRepository cityRepository,
            PlaceRepository placeRepository,
            SeatRepository seatRepository,
            SeatReservedRepository seatReservedRepository
    ) {
        this.departureRepository = departureRepository;
        this.companyRepository = companyRepository;
        this.transportMeansRepository = transportMeansRepository;
        this.cityRepository = cityRepository;
        this.placeRepository = placeRepository;
        this.seatRepository = seatRepository;
        this.seatReservedRepository = seatReservedRepository;
    }

    // =========================
    // CREATE DEPARTURE
    // =========================

    @Transactional
    public Departure createDeparture(DepartureCreateRequest req) {

        if (req == null) {
            throw new IllegalArgumentException("Request body is required");
        }

        validateRequest(req);

        if (departureRepository.existsByCompany_CompanyIdAndDepartureReference(
                req.companyId, req.departureReference)) {
            throw new RuntimeException("departureReference already exists for this company");
        }

        Company company = companyRepository.findById(req.companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        TransportMeans means = transportMeansRepository.findById(req.meansId)
                .orElseThrow(() -> new RuntimeException("TransportMeans not found"));

        City city = cityRepository.findById(req.departureCityId)
                .orElseThrow(() -> new RuntimeException("City not found"));

        Place boardingPlace = placeRepository.findById(req.departureBoardingPlaceId)
                .orElseThrow(() -> new RuntimeException("Boarding place not found"));

        Departure departure = new Departure();

        departure.setCompany(company);
        departure.setMeans(means);
        departure.setDepartureCity(city);
        departure.setDepartureBoardingPlace(boardingPlace);
        departure.setDepartureReference(req.departureReference);
        departure.setDepartureDate(req.departureDate);
        departure.setDepartureTime(req.departureTime);
        departure.setDepartureCheckinStart(req.departureCheckinStart);
        departure.setDepartureCheckinEnd(req.departureCheckinEnd);
        departure.setDepartureBoardingTime(req.departureBoardingTime);

        validateDeparture(departure);

        Departure savedDeparture = departureRepository.save(departure);

        initializeSeats(savedDeparture);

        return savedDeparture;
    }

    // =========================
    // INITIALIZE SEATS
    // =========================

    private void initializeSeats(Departure departure) {

        List<Seat> seats = seatRepository.findByMeans_MeansId(
                departure.getMeans().getMeansId()
        );

        for (Seat seat : seats) {

            boolean exists = seatReservedRepository
                    .findByDeparture_DepartureIdAndSeat_SeatId(
                            departure.getDepartureId(),
                            seat.getSeatId()
                    )
                    .isPresent();

            if (exists) {
                continue;
            }

            SeatReserved sr = new SeatReserved();

            sr.setSeat(seat);
            sr.setDeparture(departure);
            sr.setReservedStatus(SeatReservationStatus.LIBRE);
            sr.setBoardingConfirmed(BoardingConfirmed.NO);

            seatReservedRepository.save(sr);
        }
    }

    // =========================
    // UPDATE
    // =========================

    public Departure updateDeparture(String id, Departure updated) {

        return departureRepository.findById(id)
                .map(dep -> {

                    dep.setDepartureReference(updated.getDepartureReference());
                    dep.setDepartureDate(updated.getDepartureDate());
                    dep.setDepartureTime(updated.getDepartureTime());
                    dep.setDepartureCheckinStart(updated.getDepartureCheckinStart());
                    dep.setDepartureCheckinEnd(updated.getDepartureCheckinEnd());
                    dep.setDepartureBoardingTime(updated.getDepartureBoardingTime());
                    dep.setDepartureStatus(updated.getDepartureStatus());

                    validateDeparture(dep);

                    return departureRepository.save(dep);

                })
                .orElseThrow(() -> new RuntimeException("Departure not found"));
    }

    // =========================
    // DELETE
    // =========================

    public void deleteDeparture(String id) {
        departureRepository.deleteById(id);
    }

    // =========================
    // GET
    // =========================

    public List<Departure> getAllDepartures() {
        return departureRepository.findAll();
    }

    public Optional<Departure> getDepartureById(String id) {
        return departureRepository.findById(id);
    }

    public List<Departure> getDeparturesByCompany(String companyId) {
        return departureRepository.findByCompany_CompanyId(companyId);
    }

    // =========================
    // VALIDATION
    // =========================

    private void validateRequest(DepartureCreateRequest req) {

        if (req.companyId == null || req.companyId.isBlank())
            throw new RuntimeException("companyId is required");

        if (req.meansId == null || req.meansId.isBlank())
            throw new RuntimeException("meansId is required");

        if (req.departureCityId == null || req.departureCityId.isBlank())
            throw new RuntimeException("departureCityId is required");

        if (req.departureBoardingPlaceId == null || req.departureBoardingPlaceId.isBlank())
            throw new RuntimeException("departureBoardingPlaceId is required");
    }

    private void validateDeparture(Departure departure) {

        if (departure.getDepartureReference() == null || departure.getDepartureReference().isBlank())
            throw new RuntimeException("Departure reference is required");

        if (departure.getCompany() == null)
            throw new RuntimeException("Company is required");

        if (departure.getMeans() == null)
            throw new RuntimeException("Means is required");

        if (departure.getDepartureCity() == null)
            throw new RuntimeException("Departure city is required");

        if (departure.getDepartureBoardingPlace() == null)
            throw new RuntimeException("Boarding place is required");

        if (departure.getDepartureDate() == null)
            throw new RuntimeException("Departure date is required");

        if (departure.getDepartureTime() == null)
            throw new RuntimeException("Departure time is required");

        if (departure.getDepartureCheckinStart() != null &&
                departure.getDepartureCheckinEnd() != null &&
                departure.getDepartureCheckinEnd().isBefore(departure.getDepartureCheckinStart())) {

            throw new RuntimeException("checkinEnd cannot be before checkinStart");
        }

        if (departure.getDepartureBoardingTime() != null &&
                departure.getDepartureTime() != null &&
                departure.getDepartureBoardingTime().isAfter(departure.getDepartureTime())) {

            throw new RuntimeException("boardingTime cannot be after departureTime");
        }
    }
}