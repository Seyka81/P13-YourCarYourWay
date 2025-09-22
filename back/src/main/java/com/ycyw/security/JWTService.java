package com.ycyw.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Service pour gérer les tokens JWT.
 *
 * Sert à :
 * - créer un token,
 * - lire les infos d’un token,
 * - vérifier si un token est valide,
 * - récupérer le nom d’utilisateur depuis un token.
 */
@Service
public class JWTService {

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private int expiration;

    /**
     * Génère un token JWT pour un utilisateur connecté.
     *
     * @param authentication infos de l’utilisateur connecté.
     * @return le token JWT sous forme de chaîne de caractères.
     */
    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        Instant expirationTime = now.plusSeconds(expiration);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(expirationTime)
                .subject(authentication.getName())
                .build();

        JwtEncoderParameters jwtEncoderParameters =
                JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);
        return this.jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
    }

    /**
     * Lit un token et renvoie ses informations (claims).
     *
     * @param token le token JWT.
     * @return les données contenues dans le token.
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Vérifie si un token est valide (signature correcte + pas expiré).
     *
     * @param token le token JWT.
     * @return true si le token est valide, sinon false.
     */
    public boolean validateToken(String token) {
        try {
            Jwt decodedJwt = jwtDecoder.decode(token);
            Instant expirationTime = decodedJwt.getExpiresAt();
            if (expirationTime == null || expirationTime.isBefore(Instant.now())) {
                return false;
            }
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Récupère le nom d’utilisateur (username) à partir du token.
     *
     * @param token le token JWT.
     * @return le nom d’utilisateur.
     */
    public String extractUsername(String token) {
        return jwtDecoder.decode(token).getClaim("sub");
    }
}
