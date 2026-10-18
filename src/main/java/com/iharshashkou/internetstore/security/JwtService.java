package com.iharshashkou.internetstore.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;
    private final long refreshExpirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs,
            @Value("${jwt.refresh-expiration-ms}") long refreshExpirationMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );
        this.expirationMs = expirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails, expirationMs, "access");
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(
                userDetails,
                refreshExpirationMs,
                "refresh"
        );
    }

    private String generateToken(
            UserDetails userDetails,
            long lifetime,
            String tokenType
    ) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + lifetime);

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userDetails.getUsername())
                .claim("tokenType", tokenType)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {
        Claims claims = extractAllClaims(token);

        return "access".equals(claims.get("tokenType", String.class))
                && claims.getSubject().equals(userDetails.getUsername())
                && claims.getExpiration().after(new Date());
    }

    public boolean isRefreshTokenValid(String token) {
        Claims claims = extractAllClaims(token);

        return "refresh".equals(
                claims.get("tokenType", String.class)
        ) && claims.getExpiration().after(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractTokenId(String token) {
        return extractAllClaims(token).getId();
    }

    public Instant extractExpiration(String token) {
        return extractAllClaims(token)
                .getExpiration()
                .toInstant();
    }
}
