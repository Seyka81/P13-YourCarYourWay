package com.ycyw.controllers;

import com.ycyw.domain.Chat;
import com.ycyw.domain.ChatStatus;
import com.ycyw.domain.Message;
import com.ycyw.domain.Role;
import com.ycyw.dto.*;
import com.ycyw.services.MailService;
import com.ycyw.services.SupportService;
import com.ycyw.services.UserService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/support")
public class SupportController {

    private final SimpMessagingTemplate template;

    @Autowired
    private UserService userService;
    @Autowired
    private MailService mailService;
    @Autowired
    private SupportService supportService;

    public SupportController(
            SimpMessagingTemplate template
    ) {
        this.template = template;
    }

    // -----------------------
    // Contact par email
    // -----------------------
    /**
     * Envoie un mail de contact via le {@link MailService}.
     *
     * @param req données du formulaire de contact (nom, email, sujet, message).
     * @return {@link ResponseEntity} avec statut HTTP 200 si l'email a bien été envoyé.
     */
    @PostMapping("/contact")
    public ResponseEntity<?> send(@Valid @RequestBody ContactRequest req) {
        mailService.sendContactMail(req.getName(), req.getEmail(), req.getSubject(), req.getMessage());
        return ResponseEntity.ok().build();
    }

    // -----------------------
    // WebSocket / STOMP
    // -----------------------
    /**
     * Liste tous les chats disponibles.
     *
     * @param token jeton d'authentification (dans le header Authorization).
     * @return liste des chats résumés ({@link ChatSummaryDTO}).
     */
    @GetMapping("/chats")
    public ResponseEntity<List<ChatSummaryDTO>> listChats(@RequestHeader("Authorization") String token) {
        List<Chat> chats = supportService.getAllChats(token);
        List<ChatSummaryDTO> out = chats.stream()
                .map(c -> new ChatSummaryDTO(
                        c.getId(),
                        c.getTitle(),
                        c.getMessages() != null ? c.getMessages().size() : 0,
                        c.getStatus()
                ))
                .toList();

        return ResponseEntity.ok(out);
    }

    /**
     * Crée un nouveau chat.
     *
     * @param token jeton d'authentification.
     * @param req   données pour créer un chat (titre, description, etc.).
     * @return le résumé du chat créé ({@link ChatSummaryDTO}).
     */
    @PostMapping("/chats")
    public ResponseEntity<ChatSummaryDTO> createChat(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid CreateChatRequest req
    ) {

        UserDTO userdto = userService.findUserByToken(token);
        Chat saved = supportService.createNewChat(userdto, req);
        ChatSummaryDTO dto = new ChatSummaryDTO(saved.getId(), saved.getTitle(), 0, saved.getStatus());

        // broadcast pour refresh la liste côté front
        if (userdto.getRole().toString().equals(Role.SUPPORT.toString())) {
            template.convertAndSend("/topic/chats/support", dto);
        }else{
            template.convertAndSend("/topic/chats", dto);
        }


        return ResponseEntity.ok(dto);
    }

    /**
     * Liste les messages associés à un chat donné.
     *
     * @param chatId identifiant du chat.
     * @return liste des messages du chat sous forme de {@link MessageDTO}.
     */
    @GetMapping("/chats/{chatId}/messages")
    public ResponseEntity<List<MessageDTO>> listMessages(@PathVariable Long chatId) {
        List<Message> messages = supportService.getMessagesWithChatId(chatId);
        List<MessageDTO> out = messages.stream()
                .map(m -> new MessageDTO(m.getId(), m.getSender(), m.getContent(), m.getSentAt()))
                .toList();
        return ResponseEntity.ok(out);
    }

    /**
     * Crée un message dans un chat donné.
     *
     * @param chatId identifiant du chat.
     * @param req    données du message (contenu, auteur).
     * @return le message créé sous forme de {@link MessageDTO}.
     * @throws ResponseStatusException si le chat est clôturé.
     */
    @PostMapping("/chats/{chatId}/messages")
    public ResponseEntity<MessageDTO> createMessage(
            @PathVariable Long chatId,
            @RequestBody @Valid SendMessageRequest req
    ) {
        Chat chat = supportService.getChat(chatId);

        if (chat.getStatus() == ChatStatus.CLOSE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce tchat est clôturé.");
        }

        //Sauvegarder le message asynchrone
        Message saved = supportService.saveMessage(chat, req);

        // 1) diffusion du message dans la room
        ChatMessage wsMsg = new ChatMessage();
        wsMsg.setSender(saved.getSender());
        wsMsg.setContent(saved.getContent());
        template.convertAndSend("/topic/chats/" + chatId, wsMsg);

        // 2) MAJ de la liste (messageCount)
        long count = supportService.countMessages(chatId);
        ChatSummaryDTO dto = new ChatSummaryDTO(chat.getId(), chat.getTitle(), count, chat.getStatus());
        template.convertAndSend("/topic/chats", dto);

        return ResponseEntity.ok(
                new MessageDTO(saved.getId(), saved.getSender(), saved.getContent(), saved.getSentAt())
        );
    }



    /**
     * Met à jour le statut d’un chat (ex: ouvert, clôturé).
     *
     * @param chatId identifiant du chat.
     * @param req    nouvelle valeur du statut ({@link UpdateChatStatusRequest}).
     * @return résumé du chat mis à jour.
     */
    @PatchMapping("/chats/{chatId}/status")
    public ResponseEntity<ChatSummaryDTO> updateStatus(
            @PathVariable Long chatId,
            @RequestBody @Valid UpdateChatStatusRequest req
    ) {
        Chat saved = supportService.updateChatStatus(chatId, req.status());

        long count = supportService.countMessages(chatId);
        ChatSummaryDTO dto = new ChatSummaryDTO(saved.getId(), saved.getTitle(), count, saved.getStatus());

        // broadcast liste + room (comme avant)
        template.convertAndSend("/topic/chats", dto);
        template.convertAndSend("/topic/chats/" + chatId, dto);

        return ResponseEntity.ok(dto);
    }

}
