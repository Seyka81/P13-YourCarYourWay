package com.ycyw.services;

import java.util.ArrayList;
import java.util.Optional;
import com.ycyw.dto.UserDTO;

import org.springframework.http.ResponseEntity;

import com.ycyw.domain.User;
import com.ycyw.dto.UserRegistrationDTO;

public interface UserService {

    /**
     * This method should save a new user
     * @param registrationDTO
     * @return ResponseEntity<?>
     */
    ResponseEntity<?> save(UserRegistrationDTO registrationDTO);

    /**
     * This method should find a user by id
     * @param id
     * @return Optional<User>
     */
    Optional<User> findUserById(Integer id);

    /**
     * This method should find a user by email
     * @param email
     * @return User
     */
    User findUserByEmail(String email);

    /**
     * This method should find all users
     * @return ArrayList<User>
     */
    ArrayList<User> findAllUsers();

    /**
     * This method should find a user by username
     * @param username
     * @return Optional<User>
     */
    Optional<User> findUserByUsername(String username);

    /**
     * This method should find a user by token
     * @param token
     * @return UserDTO
     */
    UserDTO findUserByToken(String token);

    /**
     * This method should edit a user
     * @param id
     * @param userRegistrationDTO
     * @return Optional<User>
     */
    Optional<User> editUser(Integer id, UserRegistrationDTO userRegistrationDTO) throws Exception;
}
