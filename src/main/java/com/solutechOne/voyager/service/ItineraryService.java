package com.solutechOne.voyager.service;



import com.solutechOne.voyager.model.Itinerary;
import com.solutechOne.voyager.repositories.ItineraryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ItineraryService {

    private final ItineraryRepository repository;

    public ItineraryService(ItineraryRepository repository) {
        this.repository = repository;
    }

    public Itinerary create(Itinerary entity) {
        return repository.save(entity);
    }

    @Transactional(readOnly = true)
    public Itinerary getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Itinerary not found"));
    }

    @Transactional(readOnly = true)
    public List<Itinerary> getAll() {
        return repository.findAll();
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}