package com.solutechOne.voyager.service;

import com.solutechOne.voyager.model.Company;
import com.solutechOne.voyager.model.Tpe;
import com.solutechOne.voyager.model.TpeAcl;
import com.solutechOne.voyager.repositories.CompanyRepository;
import com.solutechOne.voyager.repositories.TpeAclRepository;
import com.solutechOne.voyager.repositories.TpeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TpeAclService {

    private final TpeAclRepository repository;
    private final CompanyRepository companyRepository;
    private final TpeRepository tpeRepository;

    public TpeAclService(TpeAclRepository repository,
                         CompanyRepository companyRepository,
                         TpeRepository tpeRepository) {
        this.repository = repository;
        this.companyRepository = companyRepository;
        this.tpeRepository = tpeRepository;
    }

    public TpeAcl create(TpeAcl acl) {

        if (acl.getCompanyId() == null || acl.getCompanyId().isBlank()) {
            throw new RuntimeException("companyId obligatoire");
        }

        if (acl.getTpeId() == null || acl.getTpeId().isBlank()) {
            throw new RuntimeException("tpeId obligatoire");
        }

        if (acl.getPlaceId() == null || acl.getPlaceId().isBlank()) {
            throw new RuntimeException("placeId obligatoire");
        }

        Company company = companyRepository.findById(acl.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company introuvable"));

        Tpe tpe = tpeRepository.findById(acl.getTpeId())
                .orElseThrow(() -> new RuntimeException("TPE introuvable"));

        acl.setCompany(company);
        acl.setTpe(tpe);

        return repository.save(acl);
    }

    public TpeAcl getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ACL not found"));
    }

    public List<TpeAcl> getAll() {
        return repository.findAll();
    }

    public List<TpeAcl> getByCompany(String companyId) {
        return repository.findByCompany_CompanyId(companyId);
    }

    public List<TpeAcl> getByTpe(String tpeId) {
        return repository.findByTpe_TpeId(tpeId);
    }

    public List<TpeAcl> getByPlace(String placeId) {
        return repository.findByPlaceId(placeId);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}