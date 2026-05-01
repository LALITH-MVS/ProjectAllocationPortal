package com.project.project_management.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 🔐 Secret key (at least 32 characters)
    private final String SECRET = "mysecretkeymysecretkeymysecretkey12345";

    // ⏱ Token validity — 24 hours (was 1 hour, too short for dev)
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    // 🔹 Get signing key
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // 🔹 Generate Token
    public String generateToken(UserDetails userDetails) {

        // ✅ FIX: Extract clean role string instead of List.toString()
        // Before: userDetails.getAuthorities().toString() → "[ROLE_STUDENT]" (with brackets)
        // After:  "ROLE_STUDENT" (clean)
        String role = userDetails.getAuthorities()
                .stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("ROLE_STUDENT");

        return Jwts.builder()
                .setSubject(userDetails.getUsername())    // email
                .claim("role", role)                      // ✅ clean role: "ROLE_STUDENT"
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 🔹 Extract Email
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    // 🔹 Validate Token
    public boolean validateToken(String token, String email) {
        return email.equals(extractEmail(token)) && !isTokenExpired(token);
    }

    // 🔹 Check Expiry
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // 🔹 Extract Claims
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}