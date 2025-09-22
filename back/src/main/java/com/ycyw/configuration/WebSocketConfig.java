package com.ycyw.configuration;

import com.ycyw.security.WebSocketAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration pour activer et gérer le système WebSocket.
 *
 * Ici on règle les canaux, les adresses, et la sécurité.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    /**
     * Constructeur qui reçoit l’intercepteur d’authentification.
     *
     * @param webSocketAuthInterceptor vérifie que l’utilisateur est bien connecté
     *                                 quand il utilise le WebSocket.
     */
    public WebSocketConfig(WebSocketAuthInterceptor webSocketAuthInterceptor) {
        this.webSocketAuthInterceptor = webSocketAuthInterceptor;
    }

    /**
     * Configure les chemins pour envoyer et recevoir les messages.
     *
     * @param config outil pour régler le broker (le "distributeur" de messages).
     */
    @SuppressWarnings("null")
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue"); // pour recevoir les messages
        config.setApplicationDestinationPrefixes("/app"); // pour envoyer les messages
        config.setUserDestinationPrefix("/user"); // pour les messages privés
    }

    /**
     * Déclare le point d’entrée WebSocket.
     *
     * @param registry outil pour enregistrer les points d’accès.
     */
    @SuppressWarnings("null")
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws") // chemin du WebSocket
                .setAllowedOriginPatterns("*") // autorise toutes les origines
                .withSockJS() // active SockJS pour compatibilité
                .setSuppressCors(true); // désactive les erreurs CORS
    }

    /**
     * Ajoute l’intercepteur d’authentification sur les messages entrants.
     *
     * @param registration outil pour enregistrer les filtres.
     */
    @SuppressWarnings("null")
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthInterceptor);
    }
}
