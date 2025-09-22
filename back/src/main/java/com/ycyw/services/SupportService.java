package com.ycyw.services;

import com.ycyw.domain.Chat;
import com.ycyw.domain.ChatStatus;
import com.ycyw.domain.Message;
import com.ycyw.dto.ChatMessage;
import com.ycyw.dto.CreateChatRequest;
import com.ycyw.dto.SendMessageRequest;
import com.ycyw.dto.UserDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.security.Principal;
import java.util.List;

/**
 * Service pour gérer le support (chats et messages).
 *
 * Permet de :
 * - créer et récupérer des chats,
 * - lister les chats,
 * - lister les messages d’un chat,
 * - changer le statut d’un chat,
 * - compter les messages,
 * - enregistrer de nouveaux messages.
 */
public interface SupportService {

    /**
     * Récupère un chat par son id.
     *
     * @param chatId identifiant du chat.
     * @return le chat trouvé.
     */
    Chat getChat(Long chatId);

    /**
     * Sauvegarde un chat avec un nouveau message.
     *
     * @param chat le chat concerné.
     * @param msg  le message à ajouter.
     * @param p    l’utilisateur qui envoie.
     */
    void saveChat(Chat chat, ChatMessage msg, Principal p);

    /**
     * Récupère tous les chats accessibles pour un utilisateur.
     *
     * @param token le jeton de l’utilisateur.
     * @return la liste des chats.
     */
    List<Chat> getAllChats(String token);

    /**
     * Crée un nouveau chat.
     *
     * @param userdto infos de l’utilisateur créateur.
     * @param req     données pour créer le chat.
     * @return le chat créé.
     */
    Chat createNewChat(UserDTO userdto, @Valid CreateChatRequest req);

    /**
     * Récupère tous les messages liés à un chat.
     *
     * @param chatId identifiant du chat.
     * @return la liste des messages.
     */
    List<Message> getMessagesWithChatId(Long chatId);

    /**
     * Met à jour le statut d’un chat (ex: ouvert, fermé).
     *
     * @param chatId identifiant du chat.
     * @param status nouveau statut.
     * @return le chat mis à jour.
     */
    Chat updateChatStatus(Long chatId, @NotNull ChatStatus status);

    /**
     * Compte combien de messages appartiennent à un chat.
     *
     * @param chatId identifiant du chat.
     * @return nombre de messages.
     */
    long countMessages(Long chatId);

    /**
     * Sauvegarde un nouveau message dans un chat.
     *
     * @param chat chat où enregistrer le message.
     * @param req  contenu du message.
     * @return le message sauvegardé.
     */
    Message saveMessage(Chat chat, @Valid SendMessageRequest req);
}
