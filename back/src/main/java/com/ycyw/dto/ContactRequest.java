package com.ycyw.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Requête utilisée pour envoyer un message via le formulaire de contact.
 *
 * Contient :
 * - name : le nom de la personne,
 * - email : son adresse email,
 * - subject : le sujet du message,
 * - message : le contenu du message.
 *
 * Des règles de validation sont appliquées (taille minimum/maximum, format email, etc.).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ContactRequest {

    /**
     * Nom de la personne qui envoie le message.
     */
    @NotBlank
    @Size(min = 2, max = 80)
    private String name;

    /**
     * Email de la personne (doit être valide).
     */
    @NotBlank
    @Email
    @Size(max = 120)
    private String email;

    /**
     * Sujet du message.
     */
    @NotBlank
    @Size(min = 3, max = 120)
    private String subject;

    /**
     * Contenu du message (texte écrit par l’utilisateur).
     */
    @NotBlank
    @Size(min = 10, max = 5000)
    private String message;
}
