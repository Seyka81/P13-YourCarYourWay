package com.ycyw.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.util.List;

/**
 * Représente un chat (conversation).
 *
 * Un chat contient :
 * - un id (identifiant unique),
 * - un titre,
 * - un propriétaire (celui qui a créé),
 * - un statut (ouvert, fermé...),
 * - une liste de messages.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "chat")
public class Chat {

    /** Identifiant unique du chat. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Titre du chat (nom de la conversation). */
    @Column(nullable = false, length = 255)
    private String title;

    /** Propriétaire du chat (créateur) — email de l'utilisateur. */
    @Column(nullable = false, length = 255)
    private String owner;

    /** Statut du chat (par défaut : ouvert). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatStatus status = ChatStatus.OPEN;

    /** Liste des messages liés à ce chat. */
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages;
}
