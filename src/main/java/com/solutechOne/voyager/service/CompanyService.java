    package com.solutechOne.voyager.service;

    import com.solutechOne.voyager.dto.CompanyDTO;
    import com.solutechOne.voyager.enums.CompanyStatus;
    import com.solutechOne.voyager.model.Company;
    import com.solutechOne.voyager.repositories.CompanyRepository;
    import jakarta.persistence.EntityNotFoundException;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import jakarta.annotation.PostConstruct;
    import org.springframework.transaction.support.TransactionSynchronizationAdapter;
    import org.springframework.transaction.support.TransactionSynchronizationManager;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.StandardCopyOption;
    import java.util.UUID;
    import java.util.Map;
    import java.util.concurrent.atomic.AtomicLong;

    import java.util.List;
    import java.util.Random;

    @Service
    @Transactional
    public class CompanyService {

        private final CompanyRepository companyRepository;
        private final BCryptPasswordEncoder passwordEncoder;
        private final NotificationService notificationService;

        public CompanyService(
                CompanyRepository companyRepository,
                BCryptPasswordEncoder passwordEncoder,
                NotificationService notificationService
        ) {
            this.companyRepository = companyRepository;
            this.passwordEncoder = passwordEncoder;
            this.notificationService = notificationService;
        }

        /* ===================== MAPPING ===================== */

        private CompanyDTO toDto(Company c) {
            CompanyDTO dto = new CompanyDTO();
            dto.setId(c.getCompanyId());
            dto.setType(c.getType());
            dto.setName(c.getName());
            dto.setUrlLogo(c.getUrlLogo());
            dto.setNif(c.getNif());
            dto.setRccm(c.getRccm());
            dto.setAppId(c.getAppId());
            dto.setAddress(c.getAddress());
            dto.setPhone(c.getPhone());
            dto.setEmail(c.getEmail());
            dto.setAggrement(c.getAggrement());
            dto.setRateFees(c.getRateFees());
            dto.setStatus(c.getStatus());
            return dto;
        }

        private void updateEntityFromDto(CompanyDTO dto, Company c) {
            if (dto.getType() != null) c.setType(dto.getType());
            if (dto.getName() != null) c.setName(dto.getName());
            if (dto.getUrlLogo() != null) c.setUrlLogo(dto.getUrlLogo());
            if (dto.getNif() != null) c.setNif(dto.getNif());
            if (dto.getRccm() != null) c.setRccm(dto.getRccm());
            if (dto.getAppId() != null) c.setAppId(dto.getAppId());
            if (dto.getAddress() != null) c.setAddress(dto.getAddress());
            if (dto.getPhone() != null) c.setPhone(dto.getPhone());
            if (dto.getEmail() != null) c.setEmail(dto.getEmail());
            if (dto.getAggrement() != null) c.setAggrement(dto.getAggrement());
            if (dto.getRateFees() != null) c.setRateFees(dto.getRateFees());
            if (dto.getStatus() != null) c.setStatus(dto.getStatus());
        }

        /* ===================== MÉTIER ===================== */

        /** Création par Manager */
        public CompanyDTO create(CompanyDTO dto) {

            if (companyRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Email compagnie déjà utilisé");
            }

            Company company = new Company();

            // ✅ ID généré si absent
            if (company.getCompanyId() == null || company.getCompanyId().isBlank()) {
                company.setCompanyId(generateCompanyId());
            }

            updateEntityFromDto(dto, company);

            // ✅ Mot de passe en clair à envoyer (doit être final pour afterCommit)
            final String passwordToSend =
                    (dto.getPassword() == null || dto.getPassword().isBlank())
                            ? generatePassword(10)
                            : dto.getPassword();

            company.setPassword(passwordEncoder.encode(passwordToSend));
            company.setStatus(CompanyStatus.ACTIVE);

            Company saved = companyRepository.save(company);

            // ✅ Envoi email uniquement après COMMIT (comme ManagerService)
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    notificationService.envoyerIdentifiantsCompany(saved, passwordToSend);
                }
            });

            return toDto(saved);
        }


        @Transactional(readOnly = true)
        public List<CompanyDTO> findAll() {
            return companyRepository.findAll().stream().map(this::toDto).toList();
        }

        @Transactional(readOnly = true)
        public CompanyDTO findById(String id) {
            return toDto(companyRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Compagnie introuvable")));
        }

        /** Update partiel */
        public CompanyDTO patch(String id, CompanyDTO dto) {
            Company company = companyRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Compagnie introuvable"));

            updateEntityFromDto(dto, company);
            return toDto(companyRepository.save(company));
        }

        /** Suspension */
        public void suspend(String id) {
            Company c = companyRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Compagnie introuvable"));
            c.setStatus(CompanyStatus.SUSPENDUE);
            companyRepository.save(c);
        }

        /** Réactivation */
        public void reactivate(String id) {
            Company c = companyRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Compagnie introuvable"));
            c.setStatus(CompanyStatus.ACTIVE);
            companyRepository.save(c);

        }

        /* ===================== UTIL ===================== */

        // Compteur thread-safe ; valeur initiale = 500000 (donc premier ID généré = 500001)
        private static final AtomicLong LAST_COMPANY_ID = new AtomicLong(500000);

        @PostConstruct
        public void initLastCompanyId() {
            // Calage du compteur sur le max existant en base (uniquement sur 6 chiffres commençant par 5)
            long max = 500000L;

            var all = companyRepository.findAll();
            for (Company c : all) {
                try {
                    String id = c.getCompanyId();
                    if (id != null && id.matches("5\\d{5}")) { // 6 chiffres et commence par 7
                        long val = Long.parseLong(id);
                        if (val > max) max = val;
                    }
                } catch (NumberFormatException ignored) {}
            }

            LAST_COMPANY_ID.set(max);
        }

        private String generateCompanyId() {
            String id;
            do {
                long next = LAST_COMPANY_ID.incrementAndGet();
                id = String.format("%06d", next); // ex: 500001
            } while (companyRepository.existsById(id));
            return id;
        }


        private String generatePassword(int length) {
            String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            StringBuilder sb = new StringBuilder();
            Random r = new Random();
            for (int i = 0; i < length; i++) {
                sb.append(chars.charAt(r.nextInt(chars.length())));
            }
            return sb.toString();
        }

        public void delete(String id) {
            if (!companyRepository.existsById(id)) {
                throw new EntityNotFoundException("Compagnie introuvable: " + id);
            }
            companyRepository.deleteById(id);
        }
        /**
         * Archive une compagnie (soft delete)
         */
        public Map<String, String> archive(String companyId) {
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new EntityNotFoundException("Compagnie introuvable: " + companyId));

            if (company.getStatus() == CompanyStatus.ARCHIVE) {
                throw new IllegalArgumentException("La compagnie est déjà archivée");
            }

            company.setStatus(CompanyStatus.ARCHIVE);
            companyRepository.save(company);

            return Map.of(
                    "status", "success",
                    "message", "La compagnie " + company.getEmail() + " a été archivée avec succès"
            );
        }

        /**
         * Désarchive une compagnie (remise en ACTIVE)
         */
        public Map<String, String> unarchive(String companyId) {
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new EntityNotFoundException("Compagnie introuvable: " + companyId));

            if (company.getStatus() != CompanyStatus.ARCHIVE) {
                throw new IllegalArgumentException("La compagnie n’est pas archivée");
            }

            company.setStatus(CompanyStatus.ACTIVE);
            companyRepository.save(company);

            return Map.of(
                    "status", "success",
                    "message", "La compagnie " + company.getEmail() + " a été désarchivée et réactivée"
            );
        }
        public CompanyDTO uploadLogo(String companyId, MultipartFile file) {

            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("Fichier logo manquant");
            }

            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new EntityNotFoundException("Compagnie introuvable: " + companyId));

            // 🔒 Sécurité basique
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Le fichier doit être une image");
            }

            try {
                // 📁 dossier: uploads/companies/{id}
                Path uploadDir = Path.of("uploads", "compagnies", companyId);
                Files.createDirectories(uploadDir);

                // 🖼 nom de fichier unique
                String extension = file.getOriginalFilename()
                        .substring(file.getOriginalFilename().lastIndexOf("."));

                String filename = "logo-" + UUID.randomUUID() + extension;
                Path targetPath = uploadDir.resolve(filename);

                // 💾 copie du fichier
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                // 🌐 URL publique (à adapter selon ton serveur)
                String logoUrl = "/uploads/compagnies/" + companyId + "/" + filename;

                company.setUrlLogo(logoUrl);
                companyRepository.save(company);

                return toDto(company);

            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de l’upload du logo", e);
            }
        }


    }
