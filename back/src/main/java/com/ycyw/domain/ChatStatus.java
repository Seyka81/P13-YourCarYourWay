package com.ycyw.domain;

/**
 * Statut possible d’un chat.
 *
 * - OPEN : le chat est ouvert (on peut encore écrire dedans).
 * - CLOSE : le chat est fermé (plus de nouveaux messages).
 */
public enum ChatStatus {
    OPEN,
    CLOSE
}
