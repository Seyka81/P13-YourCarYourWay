package com.ycyw.dto;

import com.ycyw.domain.ChatStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Requête pour changer l’état d’un chat.
 *
 * Contient :
 * - status : le nouveau statut du chat (obligatoire).
 */
public record UpdateChatStatusRequest(
        @NotNull ChatStatus status
) {}
