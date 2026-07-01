package com.evidentra.security;

import com.evidentra.config.SecurityProperties;
import com.evidentra.domain.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final SecurityProperties securityProperties;

    public String generateToken(UserDetails userDetails, UUID userId, Role role) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(securityProperties.getExpiration());
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .issuer(securityProperties.getIssuer())
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .claim("userId", userId.toString())
                .claim("role", role.name())
                .claim("authorities", authorities)
                .signWith(signingKey())
                .compact();
    }

    public Instant getExpirationInstant() {
        return Instant.now().plus(securityProperties.getExpiration());
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .requireIssuer(securityProperties.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey signingKey() {
        byte[] keyBytes = securityProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
