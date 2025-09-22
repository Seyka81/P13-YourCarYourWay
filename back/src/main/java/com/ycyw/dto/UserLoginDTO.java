package com.ycyw.dto;

import lombok.Data;

/**
 * Représente les informations nécessaires pour la connexion d’un utilisateur.
 *
 * Contient :
 * - email : l’adresse email de l’utilisateur,
 * - password : son mot de passe.
 */
@Data
public class UserLoginDTO {

    /**
     * Adresse email de l’utilisateur.
     */
    private String email;

    /**
     * Mot de passe de l’utilisateur.
     */
    private String password;
}
