package com.ycyw.security;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * Intercepteur pour sécuriser les connexions WebSocket avec STOMP.
 *
 * Il vérifie :
 * - si le client envoie bien un token JWT dans l’en-tête "Authorization",
 * - si le token est valide,
 * - il récupère l’utilisateur associé et l’ajoute au contexte du message.
 *
 * Si le token est manquant ou invalide, la connexion est refusée.
 */
@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

  private final JWTService jwtService;
  private final UserDetailsService userDetailsService;

  /**
   * Constructeur qui reçoit les services nécessaires.
   *
   * @param jwtService service pour gérer les tokens JWT.
   * @param userDetailsService service pour charger les infos utilisateur.
   */
  public WebSocketAuthInterceptor(JWTService jwtService,
                                  UserDetailsService userDetailsService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  /**
   * Vérifie les messages STOMP avant qu’ils soient envoyés.
   *
   * - Sur CONNECT : contrôle le token JWT et authentifie l’utilisateur.
   * - Sur SEND / SUBSCRIBE : refuse si l’utilisateur n’est pas authentifié.
   *
   * @param message message STOMP reçu.
   * @param channel canal du message.
   * @return le message s’il est accepté.
   */
  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor =
            MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    if (accessor == null) return message;

    // Si déjà authentifié (par le handshake), on ne fait rien
    if (accessor.getUser() != null) return message;

    // Vérifie le token au moment du CONNECT
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String bearer = first(accessor.getNativeHeader("Authorization"));
      if (bearer == null || !bearer.startsWith("Bearer "))
        throw new org.springframework.messaging.MessagingException("Missing Authorization");

      String token = bearer.substring(7);
      if (!jwtService.validateToken(token))
        throw new org.springframework.messaging.MessagingException("Invalid JWT");

      String username = jwtService.extractUsername(token);
      var user = userDetailsService.loadUserByUsername(username);
      var auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
              user, null, user.getAuthorities());

      accessor.setUser(auth);
      return message;
    }

    // Refuse SEND ou SUBSCRIBE si pas d’utilisateur authentifié
    if ((StompCommand.SEND.equals(accessor.getCommand())
            || StompCommand.SUBSCRIBE.equals(accessor.getCommand()))
            && accessor.getUser() == null) {
      throw new org.springframework.messaging.MessagingException("Unauthorized STOMP frame");
    }

    return message;
  }

  /**
   * Récupère la première valeur d’une liste (ou null si vide).
   *
   * @param l liste de chaînes.
   * @return la première valeur ou null.
   */
  private static String first(java.util.List<String> l) {
    return (l != null && !l.isEmpty()) ? l.get(0) : null;
  }
}
