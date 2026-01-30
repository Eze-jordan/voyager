package com.solutechOne.voyager.securite;

import com.solutechOne.voyager.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFiller extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtFiller(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Pas de JWT => on laisse passer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);

        try {
            String userEmail = jwtService.extractUsername(jwt);

            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

            // ✅ On autorise aussi le cas "anonymous" (sinon ton filtre ne s'exécute jamais)
            if (userEmail != null && (currentAuth == null || currentAuth instanceof AnonymousAuthenticationToken)) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {

                    // ✅ Rôle depuis le token
                    String role = jwtService.extractRole(jwt); // ex: "SUPER_ADMIN" ou "ROLE_SUPER_ADMIN"

                    // Normalisation
                    if (role != null && role.startsWith("ROLE_")) {
                        role = role.substring(5);
                    }

                    List<SimpleGrantedAuthority> authorities =
                            (role == null || role.isBlank())
                                    ? List.of()
                                    : List.of(new SimpleGrantedAuthority("ROLE_" + role));

                    // ✅ IMPORTANT : utiliser authorities (pas userDetails.getAuthorities())
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (Exception e) {
            // Optionnel : log de debug (à activer si besoin)
            // System.out.println("JWT ERROR: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
