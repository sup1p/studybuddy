package com.studybuddy.demostud.controllers;

import com.studybuddy.demostud.DTOs.FriendRequestResponse;
import com.studybuddy.demostud.DTOs.FriendsInfo;
import com.studybuddy.demostud.Service.UserService;
import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/friends")
public class FriendController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/show")
    public ResponseEntity<List<FriendsInfo>> getFriends() {
        // Get the currently authenticated user
        User authenticatedUser = getAuthenticatedUser();
        return ResponseEntity.ok(userService.getFriends(authenticatedUser.getId()));
    }

    @DeleteMapping("/delete/{friendId}")
    public ResponseEntity<String> deleteFriend(@PathVariable Long friendId) {
        User authenticatedUser = getAuthenticatedUser();
        userService.deleteFriend(authenticatedUser.getId(), friendId);
        return ResponseEntity.ok("Friendship deleted successfully");
    }

    @PostMapping("/send-request")
    public ResponseEntity<String> sendFriendRequest(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        User sender = getAuthenticatedUser();

        if (sender.getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You cannot send a friend request to yourself");
        }

        userService.sendFriendsRequest(sender.getId(), username);
        return ResponseEntity.status(HttpStatus.CREATED).body("Friend request sent");
    }

    @GetMapping("/requests/from-me")
    public ResponseEntity<List<FriendRequestResponse>> getRequestsFromMe() {
        User authenticatedUser = getAuthenticatedUser();
        return ResponseEntity.ok(userService.getRequestFromMe(authenticatedUser.getId()));
    }

    @GetMapping("/requests/to-me")
    public ResponseEntity<List<FriendRequestResponse>> getRequestsToMe() {
        User authenticatedUser = getAuthenticatedUser();
        return ResponseEntity.ok(userService.getRequestsToMe(authenticatedUser.getId()));
    }

    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<String> acceptFriendRequest(@PathVariable Long requestId) {
        userService.acceptFriendRequest(requestId);
        return ResponseEntity.ok("Friend request accepted");
    }

    @PostMapping("/requests/{requestId}/decline")
    public ResponseEntity<String> declineFriendRequest(@PathVariable Long requestId) {
        userService.declineFriendRequest(requestId);
        return ResponseEntity.ok("Friend request declined");
    }

    @DeleteMapping("/requests/{requestId}/delete")
    public ResponseEntity<String> deleteFriendRequest(@PathVariable Long requestId) {
        User authenticatedUser = getAuthenticatedUser();
        userService.deleteFriendRequest(requestId, authenticatedUser.getId());
        return ResponseEntity.ok("Friend request deleted");
    }

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }
}

