package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.dto.LoginRequest;
import com.solutechOne.voyager.securite.CustomUserDetails;
import com.solutechOne.voyager.securite.JwtResponse;
import com.solutechOne.voyager.securite.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/V1/auth")
@Tag(name = "Authentification", description = "Endpoints pour la connexion et la déconnexion")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }
    @Operation(summary = "Connexion", description = "Retourne message + token JWT")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getMotDePasse()
                    )
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            String token = jwtService.generateToken(userDetails);

            JwtResponse response = new JwtResponse(
                    "Connexion réussie",
                    token,
                    userDetails.getTypeCompte()  // MANAGER / USER / COMPANY

            );

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Email ou mot de passe incorrect");
        }
    }

    @Operation(summary = "Déconnexion", description = "Supprime le cookie JWT")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        boolean isProd = false;

        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(isProd)
                .path("/")
                .maxAge(0)
                .sameSite(isProd ? "None" : "Lax")
                .build();

        response.setHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.ok("Déconnexion réussie");
    }
}
