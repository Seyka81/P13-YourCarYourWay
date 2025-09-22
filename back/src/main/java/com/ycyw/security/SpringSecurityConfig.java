package com.ycyw.security;

import com.ycyw.services.CustomUserDetailsService;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.crypto.spec.SecretKeySpec;
import java.util.List;

/**
 * Configuration de la sécurité.
 *
 * Ici on règle :
 * - les filtres de sécurité HTTP,
 * - les règles d’accès (routes publiques / protégées),
 * - le CORS,
 * - le JWT (encoder / decoder),
 * - le chiffrement des mots de passe,
 * - l’authentification utilisateur.
 */
@Configuration
public class SpringSecurityConfig implements WebMvcConfigurer {

    @Value("${jwt.secret}")
    private String jwtKey;

    @Value("#{'${app.cors.allowed-origins:http://localhost:4200}'.split(',')}")
    private List<String> allowedOrigins;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Chaîne de sécurité dédiée au WebSocket (chemin /ws/**).
     * Tout est autorisé ici (pas d’auth obligatoire).
     *
     * @param http config HTTP.
     * @return la chaîne de filtres pour /ws/**
     * @throws Exception en cas d’erreur de config.
     */
    @Bean
    @Order(1)
    SecurityFilterChain wsChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(new AntPathRequestMatcher("/ws/**"))
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    /**
     * Chaîne de sécurité principale pour l’API.
     * - Sessions sans état (stateless),
     * - CORS activé,
     * - routes publiques (login, register, contact, docs, images),
     * - tout le reste nécessite un token JWT.
     *
     * @param http config HTTP.
     * @return la chaîne de filtres de l’API.
     * @throws Exception en cas d’erreur de config.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(Customizer.withDefaults()) // active CORS (bean plus bas)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // pré-vol CORS
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/api/support/contact").permitAll()
                        .requestMatchers("/pictures/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    // ---- CORS ----

    /**
     * Règles CORS (origines, méthodes, en-têtes, etc.).
     *
     * @return la configuration CORS utilisée par l’appli.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization","Content-Type","Accept","X-Requested-With"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Crée l’encodeur JWT (pour signer les tokens).
     *
     * @return un {@link JwtEncoder}.
     */
    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtKey.getBytes()));
    }

    /**
     * Crée le décodeur JWT (pour lire et vérifier les tokens).
     *
     * @return un {@link JwtDecoder}.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec secretKey = new SecretKeySpec(jwtKey.getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    /**
     * Encodeur de mots de passe (BCrypt).
     *
     * @return un encodeur BCrypt.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Gestionnaire d’authentification.
     * Utilise {@link CustomUserDetailsService} et BCrypt.
     *
     * @param http config HTTP partagée.
     * @param bCryptPasswordEncoder encodeur de mots de passe.
     * @return l’AuthenticationManager.
     * @throws Exception en cas d’erreur de config.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder)
            throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(customUserDetailsService).passwordEncoder(bCryptPasswordEncoder);
        return builder.build();
    }
}
