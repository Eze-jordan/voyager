package com.solutechOne.voyager.repositories;

import com.solutechOne.voyager.enums.SeatReservationStatus;
import com.solutechOne.voyager.model.SeatReserved;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface SeatReservedRepository extends JpaRepository<SeatReserved, String> {

    List<SeatReserved> findByDeparture_DepartureId(String departureId);

    List<SeatReserved> findByDeparture_DepartureIdAndReservedStatus(String departureId, Enum reservedStatus);

    Optional<SeatReserved> findByDeparture_DepartureIdAndSeat_SeatId(String departureId, String seatId);

    Optional<SeatReserved> findByReservation_ReservationId(String reservationId);

    List<SeatReserved> findByDeparture_DepartureIdAndReservedStatus(String departureId, SeatReservationStatus reservedStatus);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SeatReserved> findFirstByDeparture_DepartureIdAndReservedStatusOrderBySeat_SeatOrderNumberAsc(
            String departureId,
            SeatReservationStatus status
    );
}