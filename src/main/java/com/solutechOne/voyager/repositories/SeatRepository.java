package com.solutechOne.voyager.repositories;

import com.solutechOne.voyager.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, String> {

    List<Seat> findByMeans_MeansId(String meansId);

    List<Seat> findByTravelClass_ClassId(String classId);

    int countByMeans_MeansId(String meansId);

    boolean existsByMeans_MeansIdAndSeatReference(String meansId, String seatReference);
    boolean existsByMeans_MeansIdAndSeatOrderNumber(String meansId, Integer seatOrderNumber);

    boolean existsByMeans_MeansId(String meansId);
    void deleteByMeans_MeansId(String meansId);

}