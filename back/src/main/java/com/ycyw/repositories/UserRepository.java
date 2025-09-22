package com.ycyw.repositories;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ycyw.domain.User;
import org.springframework.stereotype.Repository;

/**
 * Permet d’accéder aux données des utilisateurs dans la base.
 *
 * Hérite de {@link JpaRepository} pour avoir les fonctions de base
 * (ajouter, modifier, supprimer, chercher par id...).
 *
 * Ajoute aussi des méthodes pour chercher un utilisateur par email,
 * récupérer son nom, et mettre à jour ses infos.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Cherche un utilisateur grâce à son email.
     *
     * @param email adresse email de l’utilisateur.
     * @return l’utilisateur trouvé ou null si absent.
     */
    User findByEmail(String email);

    /**
     * Récupère uniquement le nom d’un utilisateur avec son id.
     *
     * @param id identifiant de l’utilisateur.
     * @return l’utilisateur avec uniquement son nom.
     */
    User findUsernameById(Integer id);

    /**
     * Met à jour les informations d’un utilisateur
     * (email, nom, mot de passe, date de mise à jour).
     *
     * @param email nouvelle adresse email
     * @param name nouveau nom
     * @param password nouveau mot de passe
     * @param updated_at date de mise à jour
     * @param id identifiant de l’utilisateur
     */
    @Modifying
    @Query("UPDATE User u SET u.email = :email, u.name = :name, u.password = :password, u.updated_at = :updated_at WHERE u.id = :id")
    void updateUser(String email, String name, String password, LocalDate updated_at, Integer id);
}
