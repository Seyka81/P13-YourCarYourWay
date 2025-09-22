package com.ycyw.dto;

import com.ycyw.domain.ChatStatus;

/**
 * Données résumées d’un chat.
 *
 * Contient :
 * - id : identifiant du chat,
 * - title : titre du chat,
 * - messagesCount : nombre de messages dans le chat,
 * - status : état du chat (ouvert ou fermé).
 */
public record ChatSummaryDTO(
        Long id,
        String title,
        long messagesCount,
        ChatStatus status
) {}
