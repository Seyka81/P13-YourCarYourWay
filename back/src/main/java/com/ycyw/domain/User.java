package com.ycyw.domain;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représente un utilisateur de l’application.
 *
 * Un utilisateur contient :
 * - un identifiant unique,
 * - un email,
 * - un nom,
 * - un mot de passe,
 * - un rôle (support ou utilisateur),
 * - une date de création,
 * - une date de mise à jour.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User implements Serializable {

    /** Identifiant unique de l’utilisateur. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /** Adresse email de l’utilisateur. */
    @Column(length = 255, nullable = false)
    private String email;

    /** Nom de l’utilisateur. */
    @Column(length = 255, nullable = false)
    private String name;

    /** Mot de passe de l’utilisateur. */
    @Column(length = 255, nullable = false)
    private String password;

    /** Rôle de l’utilisateur (support ou utilisateur normal). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role;

    /** Date de création du compte. */
    @Column(nullable = false)
    private LocalDate created_at;

    /** Date de la dernière mise à jour du compte. */
    @Column(nullable = false)
    private LocalDate updated_at;

    @PrePersist
    void prePersist() {
        if (created_at == null) created_at = LocalDate.now();
        if (updated_at == null) updated_at = LocalDate.now();
    }

    @PreUpdate
    void preUpdate() {
        updated_at = LocalDate.now();
    }
}
