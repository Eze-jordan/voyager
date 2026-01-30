package com.solutechOne.voyager.service;

import com.solutechOne.voyager.dto.UserDTO;
import com.solutechOne.voyager.enums.UserRole;
import com.solutechOne.voyager.enums.UserStatus;
import com.solutechOne.voyager.model.Company;
import com.solutechOne.voyager.model.User;
import com.solutechOne.voyager.repositories.CompanyRepository;
import com.solutechOne.voyager.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private NotificationService notificationService;

    /* ---------- Mapping utilitaires ---------- */

    private UserDTO toDto(User e) {
        if (e == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(e.getId());
        dto.setCompanyId(e.getCompany() != null ? e.getCompany().getId() : null);
        dto.setName(e.getName());
        dto.setFirstname(e.getFirstname());
        dto.setEmail(e.getEmail());
        dto.setPhotoProfil(e.getPhotoProfil());
        dto.setRole(e.getRole());
        dto.setLastConnection(e.getLastConnection());
        dto.setStatus(e.getStatus());
        // password write-only => jamais renvoyé
        return dto;
    }

    private User toEntity(UserDTO dto) {
        if (dto == null) return null;
        User e = new User();
        e.setName(dto.getName());
        e.setFirstname(dto.getFirstname());
        e.setEmail(dto.getEmail());
        e.setPhotoProfil(dto.getPhotoProfil());

        // rôle par défaut
        e.setRole(dto.getRole() != null ? dto.getRole() : UserRole.ADMINISTRATEUR);

        // password si fourni (sinon auto dans create())
        if (dto.getPassword() != null) {
            e.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // status par défaut
        e.setStatus(UserStatus.ACTIF);
        return e;
    }

    private void updateEntityFromDto(UserDTO dto, User e) {
        if (dto.getName() != null) e.setName(dto.getName());
        if (dto.getFirstname() != null) e.setFirstname(dto.getFirstname());
        if (dto.getEmail() != null) e.setEmail(dto.getEmail());
        if (dto.getPhotoProfil() != null) e.setPhotoProfil(dto.getPhotoProfil());
        if (dto.getRole() != null) e.setRole(dto.getRole());
        if (dto.getStatus() != null) e.setStatus(dto.getStatus());
        if (dto.getPassword() != null) e.setPassword(passwordEncoder.encode(dto.getPassword()));
    }

    /* ---------- Métiers ---------- */

    /**
     * Crée un nouvel utilisateur compagnie
     * - vérifie unicité email
     * - associe à companyId
     * - rôle par défaut si absent
     * - mot de passe auto si absent
     * - envoi mail identifiants
     */
    public UserDTO create(UserDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Payload manquant");
        if (dto.getEmail() != null && userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
        }
        if (dto.getCompanyId() == null) {
            throw new IllegalArgumentException("companyId est obligatoire");
        }

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new EntityNotFoundException("Company introuvable: " + dto.getCompanyId()));

        User entity = toEntity(dto);
        entity.setCompany(company);

        // Génère et encode un mot de passe aléatoire si pas fourni
        String rawPassword = dto.getPassword();
        if (rawPassword == null || rawPassword.isBlank()) {
            rawPassword = generatePassword(10);
            entity.setPassword(passwordEncoder.encode(rawPassword));
        }
        if (entity.getId() == null) {
            entity.setId(generateUserId());
        }

        User saved = userRepository.save(entity);

        // Envoi email identifiants
        notificationService.envoyerIdentifiantsUser(saved, rawPassword);

        return toDto(saved);
    }


    private String generateUserId() {
        return "user-" + java.util.UUID.randomUUID();
    }

    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public UserDTO findById(String id) {
        User e = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User introuvable: " + id));
        return toDto(e);
    }

    /**
     * Mise à jour partielle
     */
    public UserDTO patch(String id, UserDTO dto) {
        User e = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User introuvable: " + id));

        if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(e.getEmail())
                && userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
        }

        // si companyId fourni => changer de company
        if (dto.getCompanyId() != null && (e.getCompany() == null || !dto.getCompanyId().equals(e.getCompany().getId()))) {
            Company company = companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new EntityNotFoundException("Company introuvable: " + dto.getCompanyId()));
            e.setCompany(company);
        }

        updateEntityFromDto(dto, e);
        return toDto(userRepository.save(e));
    }

    public void delete(String id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User introuvable: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Changement de mot de passe (self-service)
     */
    public void changePassword(String userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User introuvable: " + userId));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Ancien mot de passe incorrect");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Le nouveau mot de passe ne peut pas être vide");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /* ---------- Password generator ---------- */
    private String generatePassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }


    public UserDTO updatePhoto(String userId, MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Fichier photo manquant");
        }

        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equals("image/png") || contentType.equals("image/jpeg") || contentType.equals("image/webp"))) {
            throw new IllegalArgumentException("Format non supporté. Utilisez PNG/JPG/WEBP");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User introuvable: " + userId));

        try {
            // dossier local: uploads/profile
            Path uploadDir = Paths.get("uploads", "profiles");
            Files.createDirectories(uploadDir);

            // nom unique
            String ext = contentType.equals("image/png") ? ".png"
                    : contentType.equals("image/webp") ? ".webp"
                    : ".jpg";

            String filename = "user-" + userId + "-" + UUID.randomUUID() + ext;
            Path target = uploadDir.resolve(filename);

            // sauvegarde fichier
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // on stocke une URL ou un chemin (mieux: URL publique)
            // ici: chemin relatif
            user.setPhotoProfil("/uploads/profiles/" + filename);

            return toDto(userRepository.save(user));

        } catch (Exception e) {
            throw new RuntimeException("Erreur upload photo: " + e.getMessage(), e);
        }
    }

    /* ---------- Spring Security ---------- */

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException("Aucun utilisateur ne correspond à cet identifiant")
        );
    }
}
