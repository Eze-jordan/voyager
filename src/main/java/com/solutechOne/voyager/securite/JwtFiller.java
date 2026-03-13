package com.solutechOne.voyager.securite;

import com.solutechOne.voyager.service.CustomPassengerDetailsService;
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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFiller extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final CustomPassengerDetailsService passengerDetailsService;

    public JwtFiller(JwtService jwtService,
                     CustomUserDetailsService userDetailsService,
                     CustomPassengerDetailsService passengerDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.passengerDetailsService = passengerDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);

        try {
            String email = jwtService.extractUsername(jwt);
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

            if (email != null && (currentAuth == null || currentAuth instanceof AnonymousAuthenticationToken)) {

                String entityType = jwtService.extractTypeCompte(jwt);
                String role = jwtService.extractRole(jwt);

                List<SimpleGrantedAuthority> authorities =
                        (role == null || role.isBlank())
                                ? List.of()
                                : List.of(new SimpleGrantedAuthority("ROLE_" + role));

                if ("USER".equals(entityType)
                        || "MANAGER".equals(entityType)
                        || "COMPANY".equals(entityType)) {

                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }

                } else if ("PASSENGER".equals(entityType)) {

                    UserDetails passengerDetails = passengerDetailsService.loadUserByUsername(email);

                    if (jwtService.isTokenValid(jwt, passengerDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(passengerDetails, null, authorities);

                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        } catch (Exception e) {
            // optionnel: logger ici
        }

        filterChain.doFilter(request, response);
    }
}