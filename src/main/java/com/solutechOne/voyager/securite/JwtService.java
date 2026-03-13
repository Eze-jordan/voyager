package com.solutechOne.voyager.securite;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey secretKey =
            Keys.hmacShaKeyFor("01ca69cc7457f12adaaaf6e9e94eca1a7dd5f60d85d012eb282fe452b9202c69".getBytes());

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Générer un token pour un CustomUserDetails
    public String generateToken(CustomUserDetails userDetails, String typeCompte) {
        String role = userDetails.getRole();  // Utilisation du rôle depuis CustomUserDetails

        return Jwts.builder()
                .setSubject(userDetails.getUsername())          // email
                .claim("id", userDetails.getId())               // String (manager) ou String.valueOf(long)
                .claim("typeCompte", typeCompte)                // "USER" ou "PASSENGER"
                .claim("role", role)                            // ex: "SUPER_ADMIN" ou "USER"
                .claim("companyId", userDetails.getCompanyId()) // String ou null
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 10)) // 10h
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extraction des informations
    public String extractId(String token) {
        return extractClaim(token, claims -> claims.get("id", String.class));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public String extractTypeCompte(String token) {
        return extractClaim(token, claims -> claims.get("typeCompte", String.class));
    }

    public String extractCompanyId(String token) {
        return extractClaim(token, claims -> claims.get("companyId", String.class));
    }
}