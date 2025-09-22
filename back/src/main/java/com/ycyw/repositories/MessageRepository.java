package com.ycyw.repositories;

import com.ycyw.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Permet d’accéder aux données des messages dans la base.
 *
 * Hérite de {@link JpaRepository} pour avoir les fonctions de base
 * (ajouter, modifier, supprimer, chercher par id...).
 *
 * Ajoute aussi des méthodes pour chercher et compter les messages d’un chat.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Cherche tous les messages d’un chat donné,
     * triés du plus ancien au plus récent.
     *
     * @param chatId identifiant du chat.
     * @return liste des messages du chat.
     */
    List<Message> findByChatIdOrderBySentAtAsc(Long chatId);

    /**
     * Compte combien de messages sont liés à un chat donné.
     *
     * @param chatId identifiant du chat.
     * @return nombre de messages du chat.
     */
    long countByChatId(Long chatId);
}
