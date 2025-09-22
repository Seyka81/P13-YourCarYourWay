package com.ycyw.repositories;

import com.ycyw.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Permet d’accéder aux données des chats dans la base.
 *
 * Hérite de {@link JpaRepository} pour avoir les fonctions de base
 * (ajouter, modifier, supprimer, chercher par id...).
 *
 * Ajoute aussi une méthode pour chercher les chats d’un utilisateur précis.
 */
@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    /**
     * Cherche tous les chats créés par un utilisateur donné.
     *
     * @param owner nom ou identifiant du créateur du chat.
     * @return liste des chats appartenant à cet utilisateur.
     */
    List<Chat> findByOwner(String owner);
}
