package com.example.springsecurityjwt.config;

import com.example.springsecurityjwt.helper.Constants;
import com.example.springsecurityjwt.model.CustomUserDetails;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {
    private final String JWT_SECRET = "lodaaaaaa";

    public String generateToken(CustomUserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("userName", userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date((new Date()).getTime() + getTimeToEndOfDay()))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    public String getUserNameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    public long getTimeToEndOfDay() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(Constants.MAX_EXPIRATION_TIME);
        Duration duration = Duration.between(start, end);
        return duration.toMillis();
    }
}
