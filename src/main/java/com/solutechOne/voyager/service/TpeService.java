package com.solutechOne.voyager.service;

import com.solutechOne.voyager.model.Company;
import com.solutechOne.voyager.model.Tpe;
import com.solutechOne.voyager.repositories.CompanyRepository;
import com.solutechOne.voyager.repositories.TpeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TpeService {

    private final TpeRepository repository;
    private final CompanyRepository companyRepository;

    public TpeService(TpeRepository repository, CompanyRepository companyRepository) {
        this.repository = repository;
        this.companyRepository = companyRepository;
    }

    public Tpe create(Tpe tpe) {

        if (tpe.getCompanyId() == null || tpe.getCompanyId().isBlank()) {
            throw new RuntimeException("companyId obligatoire");
        }

        Company company = companyRepository.findById(tpe.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company introuvable"));

        tpe.setCompany(company);

        if (tpe.getTpeBrand() == null || tpe.getTpeBrand().isBlank()) {
            throw new RuntimeException("tpeBrand obligatoire");
        }

        if (tpe.getTpeModel() == null || tpe.getTpeModel().isBlank()) {
            throw new RuntimeException("tpeModel obligatoire");
        }

        return repository.save(tpe);
    }

    public Tpe getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("TPE not found"));
    }

    public List<Tpe> getAll() {
        return repository.findAll();
    }

    public List<Tpe> getByCompany(String companyId) {
        return repository.findAll()
                .stream()
                .filter(tpe -> tpe.getCompany().getCompanyId().equals(companyId))
                .toList();
    }

    public void updateLastConnection(String id) {
        Tpe tpe = getById(id);
        tpe.setTpeLastConx(LocalDateTime.now());
        repository.save(tpe);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}