package com.ycyw.dto;

import com.ycyw.domain.ChatStatus;
import jakarta.validation.constraints.NotBlank;

/**
 * Requête pour créer un nouveau chat.
 *
 * Contient :
 * - title : le titre du chat (obligatoire),
 * - status : le statut du chat (par défaut il sera ouvert).
 */
public record CreateChatRequest(
        @NotBlank String title,
        ChatStatus status
) {}
