package com.solutechOne.voyager.repositories;

import com.solutechOne.voyager.model.ItineraryStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItineraryStepRepository extends JpaRepository<ItineraryStep, String> {
}