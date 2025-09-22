package com.ycyw.services;

import com.ycyw.domain.User;
import com.ycyw.domain.Role;
import com.ycyw.dto.UserDTO;
import com.ycyw.dto.UserRegistrationDTO;
import com.ycyw.repositories.UserRepository;
import com.ycyw.security.JWTService;

import io.jsonwebtoken.Claims;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Implémentation du service {@link UserService}.
 *
 * Sert à gérer les utilisateurs :
 * - inscription,
 * - recherche par id, email ou token,
 * - récupération de tous les utilisateurs,
 * - modification des informations.
 */
@Transactional
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JWTService jwtService;

    /**
     * Enregistre un nouvel utilisateur (inscription).
     * - Si c’est le premier utilisateur => rôle SUPPORT,
     * - Sinon => rôle USER.
     * Vérifie aussi si l’email est déjà utilisé.
     *
     * @param registrationDTO infos d’inscription.
     * @return réponse HTTP (201 si créé, 409 si email déjà pris).
     */
    @Override
    public ResponseEntity<?> save(UserRegistrationDTO registrationDTO) {
        User user = new User();
        ArrayList<User> users = this.findAllUsers();
        for (User u : users) {
            if (u.getEmail().equals(registrationDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use");
            }
        }

        if (users.isEmpty()) {
            user.setRole(Role.SUPPORT);
        } else {
            user.setRole(Role.USER);
        }

        user.setEmail(registrationDTO.getEmail());
        user.setName(registrationDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setCreated_at(LocalDate.now());
        user.setUpdated_at(LocalDate.now());
        userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    /**
     * Cherche un utilisateur par id.
     *
     * @param id identifiant de l’utilisateur.
     * @return utilisateur trouvé ou vide si absent.
     */
    @Override
    public Optional<User> findUserById(Integer id) {
        return userRepository.findById(id);
    }

    /**
     * Cherche un utilisateur par email.
     *
     * @param email adresse email.
     * @return l’utilisateur trouvé.
     */
    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Récupère tous les utilisateurs.
     *
     * @return liste de tous les utilisateurs.
     */
    @Override
    public ArrayList<User> findAllUsers() {
        return (ArrayList<User>) userRepository.findAll();
    }

    /**
     * Cherche un utilisateur par son nom (ici = email).
     *
     * @param username email de l’utilisateur.
     * @return utilisateur trouvé ou vide.
     */
    @Override
    public Optional<User> findUserByUsername(String username) {
        return Optional.ofNullable(userRepository.findByEmail(username));
    }

    /**
     * Récupère un utilisateur grâce à un token JWT.
     *
     * @param token jeton JWT.
     * @return un objet {@link UserDTO} avec les infos de l’utilisateur.
     */
    @Override
    public UserDTO findUserByToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = jwtService.parseToken(token);
        String username = claims.getSubject();
        Optional<User> userOptional = findUserByUsername(username);

        if (userOptional.isPresent()) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(userOptional.get().getId());
            userDTO.setName(userOptional.get().getName());
            userDTO.setEmail(userOptional.get().getEmail());
            userDTO.setRole(userOptional.get().getRole());
            userDTO.setCreated_at(userOptional.get().getCreated_at().toString());
            userDTO.setUpdated_at(userOptional.get().getUpdated_at().toString());

            return userDTO;
        } else {
            throw new RuntimeException("User not found");
        }
    }

    /**
     * Modifie les infos d’un utilisateur existant.
     *
     * @param id identifiant de l’utilisateur.
     * @param userRegistrationDTO nouvelles données.
     * @return l’utilisateur modifié.
     * @throws Exception si l’utilisateur n’existe pas.
     */
    public Optional<User> editUser(Integer id, UserRegistrationDTO userRegistrationDTO) throws Exception {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.updateUser(
                    userRegistrationDTO.getEmail(),
                    userRegistrationDTO.getUsername(),
                    passwordEncoder.encode(userRegistrationDTO.getPassword()),
                    LocalDate.now(),
                    id
            );
        } else {
            throw new Exception("User not found");
        }
        return userRepository.findById(id);
    }
}
