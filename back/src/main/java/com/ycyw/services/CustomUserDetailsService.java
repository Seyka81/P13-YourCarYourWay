package com.ycyw.services;

import com.ycyw.domain.User;
import com.ycyw.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Service qui permet de charger un utilisateur
 * pour le système de sécurité Spring Security.
 *
 * Ici, l’utilisateur est cherché dans la base avec son email.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Cherche un utilisateur par son email et renvoie
     * un objet {@link UserDetails} utilisé par Spring Security.
     *
     * @param username l’email de l’utilisateur.
     * @return les infos de l’utilisateur sous forme {@link UserDetails}.
     * @throws UsernameNotFoundException si aucun utilisateur n’est trouvé.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList()
        );
    }
}
