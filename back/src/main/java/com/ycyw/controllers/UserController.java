package com.ycyw.controllers;

import com.ycyw.domain.User;
import com.ycyw.dto.UserDTO;
import com.ycyw.dto.UserLoginDTO;
import com.ycyw.dto.UserRegistrationDTO;
import com.ycyw.security.JWTService;
import com.ycyw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    /**
     * Méthode de connexion d'un utilisateur en base de donnée après avoir vérifié
     * que l'email et le mot de passe saisis correspondent
     *
     * @param userLoginDTO
     * @return un token autorisant les requêtes vers l'API et un statut réponse 200
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO userLoginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLoginDTO.getEmail(), userLoginDTO.getPassword()));
            String token = jwtService.generateToken(authentication);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (AuthenticationException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid login credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }


    /**
     * Méthode de création d'un utilisateur en base de donnée
     *
     * @param registrationDTO
     * @return un token autorisant les requêtes vers l'API et un statut réponse 200
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDTO registrationDTO) {

        try {
            ArrayList<User> users = userService.findAllUsers();
            for (User user : users) {
                if (user.getEmail().equals(registrationDTO.getEmail())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
                }
            }
            userService.save(registrationDTO);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(registrationDTO.getEmail(), registrationDTO.getPassword()));
            String token = jwtService.generateToken(authentication);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating user");
        }
    }

    /**
     * Méthode de récupération de l'utilisateur connecté
     *
     * @param token
     * @return un statut réponse 200
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUserByToken(@RequestHeader("Authorization") String token) {
        try {
            UserDTO userdto = userService.findUserByToken(token);
            return ResponseEntity.status(HttpStatus.OK).body(userdto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Méthode de récupération de l'utilisateur par son id
     *
     * @param id
     * @return un statut réponse 200
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Integer id) {
        return userService.findUserById(id)
                .map(user -> {
                    UserDTO userDto = new UserDTO();
                    userDto.setId(user.getId());
                    userDto.setName(user.getName());
                    userDto.setEmail(user.getEmail());
                    userDto.setCreated_at(user.getCreated_at().toString());
                    userDto.setUpdated_at(user.getUpdated_at().toString());

                    return ResponseEntity.status(HttpStatus.OK).body(userDto);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    /**
     * Méthode de suppression de l'utilisateur par son id
     *
     * @param id
     * @return un statut réponse 200
     */
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editUser(@PathVariable Integer id, @RequestBody UserRegistrationDTO userRegistrationDTO) {
        try {
            Optional<User> user =userService.editUser(id, userRegistrationDTO);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating user");
        }
    }

}
