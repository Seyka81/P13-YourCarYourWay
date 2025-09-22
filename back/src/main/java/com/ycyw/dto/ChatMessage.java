package com.ycyw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représente un message échangé dans un chat (via WebSocket).
 *
 * Il contient :
 * - le nom de l’expéditeur,
 * - le contenu du message.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {

    /**
     * Nom ou identifiant de l’expéditeur.
     */
    private String sender;

    /**
     * Contenu du message.
     */
    private String content;
}
