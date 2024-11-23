package com.studybuddy.demostud.controllers;

import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api")
public class mainController {

    private static final Logger logger = LoggerFactory.getLogger(mainController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Homepage endpoint
    @GetMapping("/homepage")
    public ResponseEntity<String> getHomepage() {
        logger.info("GET /homepage called");
        return new ResponseEntity<>("Welcome to the homepage!", HttpStatus.OK);
    }

    // Settings endpoint
    @PutMapping("/settings/{username}")
    public ResponseEntity<String> updateUserSettings(@PathVariable String username, @RequestBody User updatedSettings) {
        logger.info("PUT /settings called/{}", username);
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (updatedSettings.getUsername() != null)
                user.setUsername(updatedSettings.getUsername());
            if (updatedSettings.getEmail() != null)
                user.setEmail(updatedSettings.getEmail());
            if (updatedSettings.getCountry() != null)
                user.setCountry(updatedSettings.getCountry());
            if (updatedSettings.getDateOfBirth() != null)
                user.setDateOfBirth(updatedSettings.getDateOfBirth());
            if (updatedSettings.getLanguage() != null)
                user.setLanguage(updatedSettings.getLanguage());

            userRepository.save(user);
            return new ResponseEntity<>("User settings updated successfully.", HttpStatus.OK);
        }
        return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
    }

    // Delete user account endpoint
    @DeleteMapping("/settings/{username}/delete")
    public ResponseEntity<String> deleteUserAccount(@PathVariable String username) {
        logger.info("DELETE /{}/delete called", username);
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            userRepository.delete(userOptional.get());
            return new ResponseEntity<>("User account deleted successfully.", HttpStatus.OK);
        }
        return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
    }

    // Search system endpoint
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        logger.info("GET /search called with query: " + query);
        List<User> users = userRepository.findByUsernameContainingIgnoreCase(query);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Results of search endpoint
    @GetMapping("/search/results")
    public ResponseEntity<List<User>> getSearchResults(@RequestParam String query) {
        logger.info("GET /search/results called with query: " + query);
        List<User> users = userRepository.findByUsernameContainingIgnoreCase(query);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Chat endpoint (simplified)
    @GetMapping("/chat/{username}")
    public ResponseEntity<String> getChat(@PathVariable String username) {
        logger.info("GET /chat/{} called", username);
        return new ResponseEntity<>("Chat with " + username + " is open.", HttpStatus.OK);
    }
}
