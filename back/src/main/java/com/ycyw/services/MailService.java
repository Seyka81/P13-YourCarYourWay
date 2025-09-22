package com.ycyw.services;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Service pour gérer l’envoi des emails.
 */
public interface MailService {

    /**
     * Envoie un mail de contact.
     *
     * @param name    nom de la personne qui écrit (min 2, max 80 caractères).
     * @param email   email de la personne (doit être valide, max 120 caractères).
     * @param subject sujet du message (min 3, max 120 caractères).
     * @param message contenu du message (min 10, max 5000 caractères).
     */
    void sendContactMail(
            @NotBlank @Size(min = 2, max = 80) String name,
            @NotBlank @Email @Size(max = 120) String email,
            @NotBlank @Size(min = 3, max = 120) String subject,
            @NotBlank @Size(min = 10, max = 5000) String message
    );
}
