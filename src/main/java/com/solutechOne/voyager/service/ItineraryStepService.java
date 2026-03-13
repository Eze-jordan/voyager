package com.solutechOne.voyager.service;


import com.solutechOne.voyager.model.ItineraryStep;
import com.solutechOne.voyager.repositories.ItineraryStepRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional
public class ItineraryStepService {

    private final ItineraryStepRepository repository;

    public ItineraryStepService(ItineraryStepRepository repository) {
        this.repository = repository;
    }

    public ItineraryStep create(ItineraryStep entity) {
        return repository.save(entity);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}
