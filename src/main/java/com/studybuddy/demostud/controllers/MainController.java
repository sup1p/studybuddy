package com.studybuddy.demostud.controllers;

import com.studybuddy.demostud.Config.JwtUtils;
import com.studybuddy.demostud.DTOs.LoginResponse;
import com.studybuddy.demostud.models.LoginRequest;
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

import java.util.List;
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
    public ResponseEntity<String> registerUser(@RequestBody User user) {

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return new ResponseEntity<>("Username is already taken.", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return new ResponseEntity<>("Email is already taken.", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of((Role) roleRepository.findByRoleName("ROLE_USER")));
        userRepository.save(user);

        return new ResponseEntity<>("User registered succsesfully. ", HttpStatus.OK );
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> LoginUser(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword()
                    )
            );

            // Get UserDetails from the Authentication object
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Generate the JWT token
            String token = jwtUtils.generateToken(userDetails);

            // Return the token in the response
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

    // SETTINGS
    @PutMapping("/settings/{userId}")
    public ResponseEntity<String> updateUserSettings(@PathVariable String userId, @RequestBody User updatedSettings) {
        logger.info("PUT /settings called/{}", userId);
        User user = getAuthenticatedUser();

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


    @DeleteMapping("/settings/{userId}/delete")
    public ResponseEntity<String> deleteUserAccount(@PathVariable Long userId) {
        logger.info("DELETE /{}/delete called", userId);
        User user = getAuthenticatedUser();
        userRepository.delete(user);
        return new ResponseEntity<>("User account deleted successfully.", HttpStatus.OK);

    }

    // SEARCH(DONT WORK)
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        logger.info("GET /search called with query: " + query);
        List<User> users = userRepository.findByUsernameContainingIgnoreCase(query);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/search/results")
    public ResponseEntity<List<User>> getSearchResults(@RequestParam String query) {
        logger.info("GET /search/results called with query: " + query);
        List<User> users = userRepository.findByUsernameContainingIgnoreCase(query);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

}
