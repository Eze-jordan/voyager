package com.solutechOne.voyager.service;

import com.solutechOne.voyager.dto.*;
import com.solutechOne.voyager.exceptions.BadRequestException;
import com.solutechOne.voyager.exceptions.ConflictException;
import com.solutechOne.voyager.exceptions.NotFoundException;
import com.solutechOne.voyager.model.Company;
import com.solutechOne.voyager.model.TransportMeans;
import com.solutechOne.voyager.repositories.CompanyRepository;
import com.solutechOne.voyager.repositories.TransportMeansRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;@Service
public class TransportMeansService {

    private final TransportMeansRepository repo;
    private final CompanyRepository companyRepo;

    public TransportMeansService(TransportMeansRepository repo, CompanyRepository companyRepo) {
        this.repo = repo;
        this.companyRepo = companyRepo;
    }

    private static String newMeansId() {
        return "means-" + UUID.randomUUID();
    }

    private static String normalizeUpper(String s) {
        return s == null ? null : s.trim().toUpperCase();
    }

    private static String normalizeTrim(String s) {
        return s == null ? null : s.trim();
    }

    private static void validateCoherence(TransportMeans m) {
        if (m.getCompany() == null) throw new BadRequestException("companyId est obligatoire");
        if (m.getTransportType() == null || m.getTransportType().isBlank())
            throw new BadRequestException("transportType est obligatoire");
        if (m.getTotalSeat() == null || m.getTotalSeat() <= 0)
            throw new BadRequestException("totalSeat doit être supérieur à 0");
        if (m.getSeatOccupationMode() == null)
            throw new BadRequestException("seatOccupationMode est obligatoire");

        String t = m.getTransportType();
        if ("TERRESTRE".equals(t) && (m.getTypeVehicle() == null || m.getTypeVehicle().isBlank()))
            throw new BadRequestException("typeVehicle est obligatoire quand transportType = TERRESTRE");
        if ("MARITIME".equals(t) && (m.getTypeBoat() == null || m.getTypeBoat().isBlank()))
            throw new BadRequestException("typeBoat est obligatoire quand transportType = MARITIME");
    }

    @Transactional
    public TransportMeans create(String companyId, TransportMeans body) {

        String cId = normalizeTrim(companyId);
        if (cId == null || cId.isBlank()) throw new BadRequestException("companyId est obligatoire");

        Company company = companyRepo.findById(cId)
                .orElseThrow(() -> new NotFoundException("Compagnie introuvable"));

        // normalisations
        body.setMeansId(newMeansId());
        body.setCompany(company);

        body.setTransportType(normalizeUpper(body.getTransportType()));
        body.setTypeVehicle(normalizeTrim(body.getTypeVehicle()));
        body.setTypeBoat(normalizeTrim(body.getTypeBoat()));
        body.setRegisterId(normalizeTrim(body.getRegisterId()));
        body.setBrand(normalizeTrim(body.getBrand()));
        body.setModel(normalizeTrim(body.getModel()));
        body.setDesignation(normalizeTrim(body.getDesignation()));

        if (body.getRegisterId() == null || body.getRegisterId().isBlank()) {
            throw new BadRequestException("registerId est obligatoire");
        }

        if (repo.existsByCompany_CompanyIdAndRegisterId(cId, body.getRegisterId())) {
            throw new ConflictException("registerId déjà utilisé pour cette compagnie");
        }

        validateCoherence(body);

        return repo.save(body);
    }

    @Transactional(readOnly = true)
    public List<TransportMeans> listByCompany(String companyId) {
        String cId = normalizeTrim(companyId);
        if (cId == null || cId.isBlank()) throw new BadRequestException("companyId est obligatoire");
        return repo.findByCompany_CompanyId(cId);
    }

    @Transactional(readOnly = true)
    public TransportMeans getByIdAndCompany(String meansId, String companyId) {
        String cId = normalizeTrim(companyId);
        if (cId == null || cId.isBlank()) throw new BadRequestException("companyId est obligatoire");

        return repo.findByMeansIdAndCompany_CompanyId(meansId, cId)
                .orElseThrow(() -> new NotFoundException("Moyen de transport introuvable"));
    }

    @Transactional
    public TransportMeans update(String meansId, String companyId, TransportMeans reqBody) {
        String cId = normalizeTrim(companyId);
        if (cId == null || cId.isBlank()) throw new BadRequestException("companyId est obligatoire");

        TransportMeans m = repo.findByMeansIdAndCompany_CompanyId(meansId, cId)
                .orElseThrow(() -> new NotFoundException("Moyen de transport introuvable"));

        // registerId unique dans la compagnie
        if (reqBody.getRegisterId() != null && !reqBody.getRegisterId().trim().equals(m.getRegisterId())) {
            String newRegister = normalizeTrim(reqBody.getRegisterId());
            if (repo.existsByCompany_CompanyIdAndRegisterId(cId, newRegister)) {
                throw new ConflictException("registerId déjà utilisé pour cette compagnie");
            }
            m.setRegisterId(newRegister);
        }

        if (reqBody.getTransportType() != null) m.setTransportType(normalizeUpper(reqBody.getTransportType()));
        if (reqBody.getTypeVehicle() != null) m.setTypeVehicle(normalizeTrim(reqBody.getTypeVehicle()));
        if (reqBody.getTypeBoat() != null) m.setTypeBoat(normalizeTrim(reqBody.getTypeBoat()));
        if (reqBody.getBrand() != null) m.setBrand(normalizeTrim(reqBody.getBrand()));
        if (reqBody.getModel() != null) m.setModel(normalizeTrim(reqBody.getModel()));
        if (reqBody.getDesignation() != null) m.setDesignation(normalizeTrim(reqBody.getDesignation()));
        if (reqBody.getTotalSeat() != null) m.setTotalSeat(reqBody.getTotalSeat());
        if (reqBody.getSeatOccupationMode() != null) m.setSeatOccupationMode(reqBody.getSeatOccupationMode());
        if (reqBody.getStatus() != null) m.setStatus(reqBody.getStatus());

        validateCoherence(m);

        return repo.save(m);
    }

    @Transactional
    public void delete(String meansId, String companyId) {
        String cId = normalizeTrim(companyId);
        if (cId == null || cId.isBlank()) throw new BadRequestException("companyId est obligatoire");

        TransportMeans m = repo.findByMeansIdAndCompany_CompanyId(meansId, cId)
                .orElseThrow(() -> new NotFoundException("Moyen de transport introuvable"));

        repo.delete(m);
    }
}