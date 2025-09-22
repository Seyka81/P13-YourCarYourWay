package com.ycyw.dto;

import java.time.LocalDateTime;

/**
 * Requête pour envoyer un nouveau message dans un chat.
 *
 * Contient :
 * - sender : nom ou identifiant de la personne qui envoie,
 * - content : contenu du message,
 * - sentAt : date et heure d’envoi (peut être rempli automatiquement).
 */
public record SendMessageRequest(
        String sender,
        String content,
        LocalDateTime sentAt
) {}
