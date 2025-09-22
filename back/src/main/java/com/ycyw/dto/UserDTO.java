package com.ycyw.dto;

import com.ycyw.domain.Role;
import lombok.Data;

/**
 * Représente un utilisateur sous forme de DTO (données simplifiées).
 *
 * Contient :
 * - id : identifiant de l’utilisateur,
 * - name : nom de l’utilisateur,
 * - email : adresse email,
 * - password : mot de passe,
 * - created_at : date de création du compte,
 * - updated_at : date de mise à jour du compte,
 * - role : rôle de l’utilisateur (support ou utilisateur normal).
 */
@Data
public class UserDTO {

    /**
     * Identifiant de l’utilisateur.
     */
    protected Integer id;

    /**
     * Nom de l’utilisateur.
     */
    private String name;

    /**
     * Adresse email de l’utilisateur.
     */
    private String email;

    /**
     * Mot de passe de l’utilisateur.
     */
    private String password;

    /**
     * Date de création du compte.
     */
    private String created_at;

    /**
     * Date de mise à jour du compte.
     */
    private String updated_at;

    /**
     * Rôle de l’utilisateur.
     */
    private Role role;
}
