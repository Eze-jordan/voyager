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

    // ✅ Token complet : id + typeCompte + role + companyId
    public String generateToken(CustomUserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())          // email
                .claim("id", userDetails.getId())               // String (manager) ou String.valueOf(long)
                .claim("typeCompte", userDetails.getTypeCompte()) // MANAGER / USER / COMPANY
                .claim("role", userDetails.getRole())           // ex: ADMINISTRATEUR / SUPER_ADMIN / COMPANY
                .claim("companyId", userDetails.getCompanyId()) // String ou null ✅
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

    // ✅ Extract helpers
    public String extractId(String token) {
        // id peut être String (manager) ou num -> on le récupère comme String
        Object val = extractAllClaims(token).get("id");
        return val == null ? null : String.valueOf(val);
    }

    public String extractTypeCompte(String token) {
        return extractAllClaims(token).get("typeCompte", String.class);
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public String extractCompanyId(String token) {
        Object val = extractAllClaims(token).get("companyId");
        return val == null ? null : String.valueOf(val);
    }

}
