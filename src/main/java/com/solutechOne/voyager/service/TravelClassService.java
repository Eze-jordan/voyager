package com.solutechOne.voyager.service;

import com.solutechOne.voyager.model.Company;
import com.solutechOne.voyager.model.TravelClass;
import com.solutechOne.voyager.repositories.CompanyRepository;
import com.solutechOne.voyager.repositories.TravelClassRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TravelClassService {

    private final TravelClassRepository travelClassRepository;
    private final CompanyRepository companyRepository;

    public TravelClassService(TravelClassRepository travelClassRepository,
                              CompanyRepository companyRepository) {
        this.travelClassRepository = travelClassRepository;
        this.companyRepository = companyRepository;
    }

    private String generateClassId() {
        return "class-" + UUID.randomUUID();
    }

    public TravelClass createTravelClass(String companyId, TravelClass travelClass) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found: " + companyId));

        if (travelClass.getClassId() == null) {
            travelClass.setClassId(generateClassId());
        }
        if (travelClass.getClassStatus() == null) {
            travelClass.setClassStatus(TravelClass.ClassStatus.ACTIF);
        }

        if (travelClassRepository.existsByCompany_CompanyIdAndClassDesignation(companyId, travelClass.getClassDesignation())) {
            throw new RuntimeException("This class already exists for the company");
        }

        travelClass.setCompany(company);
        return travelClassRepository.save(travelClass);
    }

    public Optional<TravelClass> getTravelClassById(String classId) {
        return travelClassRepository.findById(classId);
    }

    public List<TravelClass> getAllTravelClasses() {
        return travelClassRepository.findAll();
    }

    public List<TravelClass> getTravelClassesByCompanyId(String companyId) {
        return travelClassRepository.findByCompany_CompanyId(companyId);
    }

    public TravelClass updateTravelClass(String classId, TravelClass updatedClass) {
        return travelClassRepository.findById(classId)
                .map(travelClass -> {
                    travelClass.setClassDesignation(updatedClass.getClassDesignation());
                    if (updatedClass.getClassStatus() != null) {
                        travelClass.setClassStatus(updatedClass.getClassStatus());
                    }
                    return travelClassRepository.save(travelClass);
                })
                .orElseThrow(() -> new RuntimeException("TravelClass not found with id " + classId));
    }

    public void deleteTravelClass(String classId) {
        travelClassRepository.deleteById(classId);
    }
}