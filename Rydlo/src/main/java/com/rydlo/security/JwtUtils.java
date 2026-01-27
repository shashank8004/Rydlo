package com.rydlo.security;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.expiration.time}")
    private long jwtExpirationTime;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        if (jwtSecret.length() < 32) {
            throw new IllegalStateException(
                "JWT secret must be at least 32 characters long"
            );
        }
        // Use UTF-8 explicitly to avoid platform dependency
        
        secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        log.info("JWT initialized with expiration {} ms", jwtExpirationTime);
    }

    public String generateToken(UserPrincipal principal) {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationTime);

        return Jwts.builder()
                .setSubject(principal.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .addClaims(Map.of(
                	    "user_id", principal.getUserId(),
                	    "roles", principal.getAuthorities()
                	            .stream()
                	            .map(GrantedAuthority::getAuthority)
                	            .toList()
                	))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateToken(String jwt) {
    	
        // Did not catch exception here to let it propagate
    	
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }


    public Long getUserId(Claims claims) {
        return claims.get("user_id", Long.class);
    }

    public String getUserRole(Claims claims) {
        return claims.get("user_role", String.class);
    }
}
