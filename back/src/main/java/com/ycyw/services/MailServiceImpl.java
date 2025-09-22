package com.ycyw.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Implémentation du service {@link MailService}.
 *
 * Sert à envoyer un email via le formulaire de contact.
 */
@Service
public class MailServiceImpl implements MailService {

    private JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    /**
     * Constructeur qui reçoit le mailSender (outil d’envoi d’emails).
     *
     * @param mailSender composant de Spring pour envoyer des emails.
     */
    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envoie un mail de contact avec le nom, l’email,
     * le sujet et le message donnés.
     *
     * @param name    nom de la personne qui envoie.
     * @param email   adresse email de la personne.
     * @param subject sujet du message.
     * @param message contenu du message.
     */
    @Override
    public void sendContactMail(String name, String email, String subject, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(fromEmail);
        mail.setBcc(fromEmail); // copie cachée à l’adresse "from"
        mail.setTo(email);
        mail.setSubject("Contact — " + subject);

        String body = """
                Nouveau message depuis le formulaire de contact

                Nom: %s
                Email: %s
                Sujet: %s

                Message:
                %s
                """.formatted(name, email, subject, message);

        mail.setText(body);
        mailSender.send(mail);
    }
}
