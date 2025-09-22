package com.ycyw.dto;

import java.time.LocalDateTime;

/**
 * Données d’un message envoyé dans un chat.
 *
 * Contient :
 * - id : identifiant unique du message,
 * - sender : nom ou identifiant de l’expéditeur,
 * - content : contenu du message,
 * - sentAt : date et heure d’envoi.
 */
public record MessageDTO(
        Long id,
        String sender,
        String content,
        LocalDateTime sentAt
) {}
