package com.studybuddy.demostud.controllers;

import com.studybuddy.demostud.Service.UserService;
import com.studybuddy.demostud.models.FriendRequest;
import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/user/friends") // Ensure all endpoints fall under /user/** for ROLE_USER and ROLE_ADMIN
public class FriendController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // Get all friends of the authenticated user
    @GetMapping("/show")
    public ResponseEntity<Set<User>> getFriends() {
        // Get the currently authenticated user
        User authenticatedUser = getAuthenticatedUser();
        return ResponseEntity.ok(userService.getFriends(authenticatedUser.getId()));
    }

    // Delete a friend for the authenticated user
    @DeleteMapping("/delete/{friendId}")
    public ResponseEntity<String> deleteFriend(@PathVariable Long friendId) {
        User authenticatedUser = getAuthenticatedUser();
        userService.deleteFriend(authenticatedUser.getId(), friendId);
        return ResponseEntity.ok("Friendship deleted successfully");
    }

    // Send a friend request to another user by username
    @PostMapping("/send-request")
    public ResponseEntity<String> sendFriendRequest(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");

        // Get the currently authenticated user
        User sender = getAuthenticatedUser();

        // Check if the user is trying to send a request to themselves
        if (sender.getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You cannot send a friend request to yourself");
        }

        // Delegate to the service to handle the business logic
        userService.sendFriendsRequest(sender.getId(), username);
        return ResponseEntity.status(HttpStatus.CREATED).body("Friend request sent");
    }

    // Get requests sent by the authenticated user
    @GetMapping("/requests/from-me")
    public ResponseEntity<List<FriendRequest>> getRequestsFromMe() {
        User authenticatedUser = getAuthenticatedUser();
        return ResponseEntity.ok(userService.getRequestFromMe(authenticatedUser.getId()));
    }

    // Get requests received by the authenticated user
    @GetMapping("/requests/to-me")
    public ResponseEntity<List<FriendRequest>> getRequestsToMe() {
        User authenticatedUser = getAuthenticatedUser();
        return ResponseEntity.ok(userService.getRequestsToMe(authenticatedUser.getId()));
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

    // Delete a friend request sent or received
    @DeleteMapping("/requests/{requestId}/delete")
    public ResponseEntity<String> deleteFriendRequest(@PathVariable Long requestId) {
        User authenticatedUser = getAuthenticatedUser();
        userService.deleteFriendRequest(requestId, authenticatedUser.getId());
        return ResponseEntity.ok("Friend request deleted");
    }

    // Helper method to get the authenticated user
    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }
}

