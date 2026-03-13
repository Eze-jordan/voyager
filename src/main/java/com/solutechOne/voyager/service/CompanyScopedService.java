package com.solutechOne.voyager.service;

import com.solutechOne.voyager.model.Company;
import com.solutechOne.voyager.repositories.CompanyRepository;
import com.solutechOne.voyager.securite.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;

public abstract class CompanyScopedService {

    protected final CompanyRepository companyRepository;

    protected CompanyScopedService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    protected String currentCompanyId() {
        return SecurityUtils.currentCompanyIdOrThrow();
    }

    protected Company currentCompanyEntity() {
        String companyId = currentCompanyId();
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company introuvable: " + companyId));
    }
}
