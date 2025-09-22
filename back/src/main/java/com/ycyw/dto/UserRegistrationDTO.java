package com.ycyw.dto;

import lombok.Data;

/**
 * Représente les informations nécessaires pour inscrire un nouvel utilisateur.
 *
 * Contient :
 * - username : le nom choisi par l’utilisateur,
 * - email : son adresse email,
 * - password : son mot de passe.
 */
@Data
public class UserRegistrationDTO {

    /**
     * Nom choisi par l’utilisateur.
     */
    private String username;

    /**
     * Adresse email de l’utilisateur.
     */
    private String email;

    /**
     * Mot de passe de l’utilisateur.
     */
    private String password;
}
