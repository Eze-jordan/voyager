package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.dto.ChangePasswordUser;
import com.solutechOne.voyager.dto.UserDTO;
import com.solutechOne.voyager.repositories.UserRepository;
import com.solutechOne.voyager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

// Swagger OpenAPI
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/V1/users")
@Tag(name = "Users", description = "Endpoints for managing company users (create, update, delete, password)")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public boolean isSelf(String userId, String email) {
        if (userId == null || email == null) return false;

        return userRepository.findById(userId)
                .map(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email))
                .orElse(false);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('COMPANY')")
    @Operation(summary = "Create a user account", tags = "Users")
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserDTO body) {
        UserDTO created = userService.create(body);
        return ResponseEntity
                .created(URI.create("/api/V1/users/" + created.getId()))
                .body(created);
    }

    @PatchMapping("/{id}/change-password")
    @PreAuthorize("""
    (hasAnyRole('ADMINISTRATEUR','COMMERCIAL','COMPTABLE','AUDITEUR') and this.isSelf(#id, authentication.name))
    or hasRole('COMPANY')
""")    @Operation(summary = "Change password for a user (self or admin)", tags = "Users")
    public ResponseEntity<Map<String, String>> changePassword(
            @PathVariable String id,
            @Valid @RequestBody ChangePasswordUser body,
            Authentication authentication
    ) {
        String connectedEmail = authentication.getName(); // email dans JWT
        UserDTO user = userService.findById(id);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRATEUR"));

        // ✅ l'utilisateur peut changer son mot de passe
        // ✅ l'admin peut changer celui de n'importe qui
        if (!user.getEmail().equalsIgnoreCase(connectedEmail) && !isAdmin) {
            throw new AccessDeniedException("Vous ne pouvez modifier que votre propre mot de passe");
        }

        userService.changePassword(id, body.getOldPassword(), body.getNewPassword());

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Mot de passe modifié avec succès"
        ));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COMPANY')")
    @Operation(summary = "Get all users", tags = "Users")
    public ResponseEntity<List<UserDTO>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("""
    (hasAnyRole('ADMINISTRATEUR','COMMERCIAL','COMPTABLE','AUDITEUR') and this.isSelf(#id, authentication.name))
    or hasRole('COMPANY')
""")    @Operation(summary = "Get user by ID", tags = "Users")
    public ResponseEntity<UserDTO> findById(@PathVariable String id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    /** Partial update: send only the fields to modify */
    @PatchMapping("/{id}")
    @PreAuthorize("""
    (hasAnyRole('ADMINISTRATEUR','COMMERCIAL','COMPTABLE','AUDITEUR') and this.isSelf(#id, authentication.name))
    or hasRole('COMPANY')
""")    @Operation(summary = "Update user (partial)", tags = "Users")
    public ResponseEntity<UserDTO> patch(@PathVariable String id, @RequestBody UserDTO body) {
        return ResponseEntity.ok(userService.patch(id, body));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY')")
    @Operation(summary = "Delete a user by ID", tags = "Users")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/photo")
    @PreAuthorize("""
    (hasAnyRole('ADMINISTRATEUR','COMMERCIAL','COMPTABLE','AUDITEUR') and this.isSelf(#id, authentication.name))
    or hasRole('COMPANY')
""")
    @Operation(summary = "Upload user profile photo", tags = "Users")
    public ResponseEntity<UserDTO> uploadPhoto(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file
    ) {
        UserDTO updated = userService.updatePhoto(id, file);
        return ResponseEntity.ok(updated);
    }

}
