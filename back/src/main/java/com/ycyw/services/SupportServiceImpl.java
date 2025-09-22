package com.ycyw.services;

import com.ycyw.domain.Chat;
import com.ycyw.domain.ChatStatus;
import com.ycyw.domain.Message;
import com.ycyw.dto.ChatMessage;
import com.ycyw.dto.CreateChatRequest;
import com.ycyw.dto.SendMessageRequest;
import com.ycyw.dto.UserDTO;
import com.ycyw.repositories.ChatRepository;
import com.ycyw.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implémentation du service {@link SupportService}.
 *
 * Sert à gérer :
 * - les chats,
 * - les messages,
 * - le statut des chats.
 */
@Service
@Transactional
public class SupportServiceImpl implements SupportService {

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserService userService;

    /**
     * Récupère un chat par son identifiant.
     */
    @Override
    public Chat getChat(Long chatId) {
        return chatRepository.findById(chatId).orElseThrow();
    }

    /**
     * Sauvegarde un nouveau message dans un chat donné.
     *
     * @param chat le chat concerné.
     * @param msg  le message reçu.
     * @param p    utilisateur connecté (peut être null).
     */
    @Override
    public void saveChat(Chat chat, ChatMessage msg, Principal p) {
        Message entity = new Message();
        entity.setChat(chat);
        entity.setSender(msg.getSender() != null ? msg.getSender() : (p != null ? p.getName() : "anonymous"));
        entity.setContent(msg.getContent());
        entity.setSentAt(LocalDateTime.now());
        messageRepository.save(entity);
    }

    /**
     * Récupère tous les chats ouverts.
     * - Si utilisateur support : tous les chats,
     * - Sinon : seulement ses propres chats.
     *
     * @param token jeton de l’utilisateur.
     * @return liste de chats.
     */
    @Override
    public List<Chat> getAllChats(String token) {
        UserDTO userdto = userService.findUserByToken(token);
        boolean isSupport = userdto.getRole().toString().equals("SUPPORT");

        List<Chat> chats = (isSupport ? chatRepository.findAll() : chatRepository.findByOwner(userdto.getEmail()))
                .stream()
                .filter(c -> c.getStatus() == ChatStatus.OPEN)
                .toList();
        return chats;
    }

    /**
     * Crée un nouveau chat.
     *
     * @param userdto infos de l’utilisateur créateur.
     * @param req     infos du chat à créer.
     * @return le chat créé.
     */
    @Override
    public Chat createNewChat(UserDTO userdto, CreateChatRequest req) {
        Chat c = new Chat();
        c.setTitle(req.title());
        c.setOwner(userdto.getEmail());
        c.setStatus(req.status() != null ? req.status() : ChatStatus.OPEN);

        return chatRepository.save(c);
    }

    /**
     * Récupère tous les messages d’un chat (triés par date).
     *
     * @param chatId identifiant du chat.
     * @return liste des messages.
     */
    @Override
    public List<Message> getMessagesWithChatId(Long chatId) {
        return messageRepository.findByChatIdOrderBySentAtAsc(chatId);
    }

    /**
     * Sauvegarde un message dans un chat.
     *
     * @param chat chat lié au message.
     * @param req  données du message.
     * @return le message enregistré.
     */
    @Override
    public Message saveMessage(Chat chat, SendMessageRequest req) {
        Message m = new Message();
        m.setChat(chat);
        m.setSender(req.sender());
        m.setContent(req.content());
        m.setSentAt(req.sentAt() != null ? req.sentAt() : LocalDateTime.now());
        return messageRepository.save(m);
    }

    /**
     * Compte combien de messages sont liés à un chat.
     *
     * @param chatId identifiant du chat.
     * @return nombre de messages.
     */
    @Override
    public long countMessages(Long chatId) {
        return messageRepository.countByChatId(chatId);
    }

    /**
     * Met à jour le statut d’un chat (ouvert/fermé).
     *
     * @param chatId identifiant du chat.
     * @param status nouveau statut.
     * @return le chat mis à jour.
     */
    @Override
    public Chat updateChatStatus(Long chatId, ChatStatus status) {
        Chat c = chatRepository.findById(chatId).orElseThrow();
        c.setStatus(status);
        return chatRepository.save(c);
    }
}
