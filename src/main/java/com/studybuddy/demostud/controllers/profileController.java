package com.studybuddy.demostud.controllers;

import com.studybuddy.demostud.Service.UserService;
import com.studybuddy.demostud.models.FriendRequest;
import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class profileController {

    private static final Logger logger = LoggerFactory.getLogger(controller.class);


    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{username}")
    public ResponseEntity<User> getUserProfile(@PathVariable String username) {
        logger.info("GET /profile/{} called", username);
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    // Эндпоинт для способностей
    @GetMapping("/{username}/skills")
    public ResponseEntity<List<String>> getUserSkills(@PathVariable Long username) {
        // Замените на реальную логику получения данных
        List<String> skills = List.of("Java", "Spring", "React");
        return ResponseEntity.ok(skills);
    }

    // Эндпоинт для проектов
    @GetMapping("/{username}/projects")
    public ResponseEntity<List<String>> getUserProjects(@PathVariable Long username) {
        // Замените на реальную логику получения данных
        List<String> projects = List.of("Project 1", "Project 2", "Project 3");
        return ResponseEntity.ok(projects);
    }


    // Get list of friends
    @GetMapping("/{userId}/friends")
    public ResponseEntity<List<User>> getFriends(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getFriends(userId));
    }

    // Send a friend request by username
    @PostMapping("/{userId}/send-request")
    public ResponseEntity<String> sendFriendRequest(
            @PathVariable Long userId,
            @RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        userService.sendFriendsRequest(userId, username);
        return ResponseEntity.status(HttpStatus.CREATED).body("Friend request sent");
    }

    // Get requests sent by the user
    @GetMapping("/{userId}/requests/from-me")
    public ResponseEntity<List<FriendRequest>> getRequestsFromMe(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getRequestFromMe(userId));
    }

    // Get requests received by the user
    @GetMapping("/{userId}/requests/to-me")
    public ResponseEntity<List<FriendRequest>> getRequestsToMe(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getRequestsToMe(userId));
    }

    // Accept a friend request
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<String> acceptFriendRequest(@PathVariable Long requestId) {
        userService.acceptFriendRequest(requestId);
        return ResponseEntity.ok("Friend request accepted");
    }

    // Decline a friend request
    @PostMapping("/requests/{requestId}/decline")
    public ResponseEntity<String> declineFriendRequest(@PathVariable Long requestId) {
        userService.declineFriendRequest(requestId);
        return ResponseEntity.ok("Friend request declined");
    }
}
