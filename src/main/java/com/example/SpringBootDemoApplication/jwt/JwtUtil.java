package com.example.SpringBootDemoApplication.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.DecodingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
//For generating and validating tokens
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-time}")
    private long expirationTime;

    @Value("${jwt.refresh-token-secret}")
    private String refreshTokenSecret;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpirationMs;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token, String tokenSecret) {
        return extractClaim(token, Claims::getExpiration, tokenSecret);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody();
        } catch (DecodingException e) {
            System.out.println("Error decoding JWT: " + e.getMessage());
            throw e;
        }
    }

    public boolean isAccessTokenExpired(String token) {
        return extractExpiration(token, secret).before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isAccessTokenExpired(token));
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), refreshTokenExpirationMs, refreshTokenSecret);
    }

    public Boolean validateRefreshToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token, refreshTokenSecret);
        return (username.equals(userDetails.getUsername()) && !isRefreshTokenExpired(token, refreshTokenSecret));
    }

    private String createToken(Map<String, Object> claims, String subject, long expirationMs, String tokenSecret) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(SignatureAlgorithm.HS256, tokenSecret)
                .compact();
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, expirationTime, secret);
    }

    public String extractUsername(String token, String tokenSecret) {
        return extractClaim(token, Claims::getSubject, tokenSecret);
    }

    public Boolean isRefreshTokenExpired(String token, String tokenSecret) {
        return extractExpiration(token, tokenSecret).before(new Date());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String tokenSecret) {
        final Claims claims = extractAllClaims(token, tokenSecret);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token, String tokenSecret) {
        return Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(token).getBody();
    }

    public String getRefreshTokenSecret() {
        return refreshTokenSecret;
    }

}
