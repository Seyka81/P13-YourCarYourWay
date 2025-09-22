package com.ycyw.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Représente un message dans un chat.
 *
 * Un message contient :
 * - un id unique,
 * - le chat auquel il appartient,
 * - l’expéditeur (sender),
 * - le contenu du message,
 * - la date et l’heure d’envoi.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "message")
public class Message {

    /** Identifiant unique du message. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Le chat auquel appartient ce message. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    @JsonBackReference
    private Chat chat;

    /** Nom ou identifiant de l’expéditeur. */
    @Column(nullable = false, length = 255)
    private String sender;

    /** Contenu du message (texte envoyé). */
    @Column(nullable = false, length = 1024)
    private String content;

    /** Date et heure où le message a été envoyé. */
    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    /** Si la date n’est pas fournie, on met maintenant. */
    @PrePersist
    protected void onCreate() {
        if (sentAt == null) {
            sentAt = LocalDateTime.now();
        }
    }
}
