package com.studybuddy.demostud.controllers;

import com.studybuddy.demostud.models.LoginRequest;
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
//REGISTER
    @PostMapping("/auth/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        logger.info("Post /auth/register called with user: " + user.getUsername());

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return new ResponseEntity<>("Username is already taken.", HttpStatus.BAD_REQUEST);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return new ResponseEntity<>("User registered succsesfully. ", HttpStatus.OK );
    }

    @PostMapping("/auth/login")
    public ResponseEntity<String> LoginUser(@RequestBody LoginRequest loginRequest) {
        logger.info("POST /auth/login called with email" + loginRequest.getEmail());

        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        if (userOptional.isPresent()){
            User user = userOptional.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return new ResponseEntity<>("Login successful." , HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Invalid email or password" , HttpStatus.UNAUTHORIZED);
    }

    // SETTINGS
    @PutMapping("/settings/{userId}")
    public ResponseEntity<String> updateUserSettings(@PathVariable String userId, @RequestBody User updatedSettings) {
        logger.info("PUT /settings called/{}", userId);
        Optional<User> userOptional = userRepository.findByUsername(userId);
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
            if (updatedSettings.getSystem_language() != null)
                user.setSystem_language(updatedSettings.getSystem_language());

            userRepository.save(user);
            return new ResponseEntity<>("User settings updated successfully.", HttpStatus.OK);
        }
        return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
    }


    @DeleteMapping("/settings/{userId}/delete")
    public ResponseEntity<String> deleteUserAccount(@PathVariable Long userId) {
        logger.info("DELETE /{}/delete called", userId);
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            userRepository.delete(userOptional.get());
            return new ResponseEntity<>("User account deleted successfully.", HttpStatus.OK);
        }
        return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
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
