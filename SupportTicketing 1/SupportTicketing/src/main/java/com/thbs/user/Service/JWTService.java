package com.thbs.user.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.thbs.user.entity.UserPrincipal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {

    // ✅ Stable RAW secret read from config/env (>= 32 characters)
    @Value("${app.jwt.hmac-secret}")
    private String secretkey;

    private Key hmacKey() {
        byte[] keyBytes = secretkey.getBytes(StandardCharsets.UTF_8); // RAW bytes
        if (keyBytes.length < 32) {
            throw new IllegalStateException("HS256 key must be >= 32 bytes");
        }
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    public String generateToken(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        // Store raw roles in token (MANAGER/AGENT/CUSTOMER). Ticket will prefix ROLE_ at runtime.
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)                 // ROLE_MANAGER
                .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a) // MANAGER
                .collect(Collectors.toList());

        Instant now = Instant.now();
        return Jwts.builder()
                .subject(principal.getUsername())
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(30, ChronoUnit.HOURS)))
                .signWith(hmacKey()) // HS256 inferred
                .compact();
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return (List<String>) claims.get("roles"); // ["CUSTOMER"] etc.
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isValid(String token) {
        try {
            extractAllClaims(token); // throws if invalid/expired
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        // JJWT 0.12.x
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretkey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}