package com.studybuddy.demostud.controllers;

import com.studybuddy.demostud.Config.JwtUtils;
import com.studybuddy.demostud.DTOs.LoginResponse;
import com.studybuddy.demostud.DTOs.RegisterRequest;
import com.studybuddy.demostud.DTOs.LoginRequest;
import com.studybuddy.demostud.models.Role;
import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.repository.RoleRepository;
import com.studybuddy.demostud.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Set;


@RestController
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public MainController(RoleRepository roleRepository, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    // Helper method to get the authenticated user
    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }


    // Homepage endpoint
    @GetMapping("/homepage")
    public ResponseEntity<String> getHomepage() {
        logger.info("GET /homepage called");
        return new ResponseEntity<>("Welcome to the homepage!", HttpStatus.OK);
    }
//REGISTER
    @PostMapping("/auth/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest registerRequest) {

        // Проверяем, есть ли уже пользователь с таким именем
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            return new ResponseEntity<>("Username is already taken.", HttpStatus.BAD_REQUEST);
        }

        // Проверяем, есть ли уже пользователь с такой почтой
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            return new ResponseEntity<>("Email is already taken.", HttpStatus.BAD_REQUEST);
        }

        // Создаём объект User и заполняем его данными из DTO
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setGender(registerRequest.getGender());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Role role = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles(Set.of(role));

        // Сохраняем пользователя
        userRepository.save(user);

        return new ResponseEntity<>("User registered successfully.", HttpStatus.OK);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> LoginUser(@RequestBody LoginRequest loginRequest) {

            // Authenticate the user 
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword()
                    )
            );

            // Get UserDetails from the Authentication object
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("не нашел почту такую мэн"));

            // Generate the JWT token
            String token = jwtUtils.generateToken(userDetails);

            // Return the token in the response
            return ResponseEntity.ok(new LoginResponse(token,user.getUsername()));


    }
    @GetMapping("/settings/gender")
    public ResponseEntity<String> showGender(){
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(user.getGender());
    }
    // SETTINGS
    @PutMapping("/settings")
    public ResponseEntity<String> updateUserSettings(@RequestBody User updatedSettings) {
        logger.info("PUT /settings called");
        User user = getAuthenticatedUser(); // Получаем текущего пользователя

        if (updatedSettings.getUsername() != null)
            user.setUsername(updatedSettings.getUsername());
        if (updatedSettings.getEmail() != null)
            user.setEmail(updatedSettings.getEmail());
        if (updatedSettings.getCountry() != null)
            user.setCountry(updatedSettings.getCountry());
        if (updatedSettings.getDateOfBirth() != null)
            user.setDateOfBirth(updatedSettings.getDateOfBirth());
        if (updatedSettings.getSystem_language() != null)
            user.setSystem_language(updatedSettings.getSystem_language());

        userRepository.save(user);
        return new ResponseEntity<>("User settings updated successfully.", HttpStatus.OK);
    }

    @DeleteMapping("/settings/delete")
    public ResponseEntity<String> deleteUserAccount() {
        logger.info("DELETE /settings/delete called");
        User user = getAuthenticatedUser(); // Получаем текущего пользователя
        userRepository.delete(user);
        return new ResponseEntity<>("User account deleted successfully.", HttpStatus.OK);
    }
}
