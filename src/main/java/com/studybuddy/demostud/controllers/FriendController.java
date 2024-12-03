package com.studybuddy.demostud.controllers;

import com.studybuddy.demostud.DTOs.FriendRequestResponse;
import com.studybuddy.demostud.DTOs.FriendsInfo;
import com.studybuddy.demostud.Service.UserService;
import com.studybuddy.demostud.enums.RequestStatus;
import com.studybuddy.demostud.models.FriendRequest;
import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.repository.FriendsRequestRepository;
import com.studybuddy.demostud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user/friends")
public class FriendController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendsRequestRepository friendsRequestRepository;

    @GetMapping("/show")//returns every friend of user
    public ResponseEntity<List<FriendsInfo>> getFriends() {
        User authenticatedUser = getAuthenticatedUser(); // in the bottom stays method for getting user and checking him for authentification
        return ResponseEntity.ok(userService.getFriends(authenticatedUser.getId()));
    }

    @DeleteMapping("/delete/{friendId}")// mapping for delete friends by his id
    public ResponseEntity<String> deleteFriend(@PathVariable Long friendId) {
        User authenticatedUser = getAuthenticatedUser();
        userService.deleteFriend(authenticatedUser.getId(), friendId);  //current users id and being deleted friends id
        return ResponseEntity.ok("Friendship deleted successfully");
    }

    @PostMapping("/send-request")  // send request of friendship to user, by his username
    public ResponseEntity<String> sendFriendRequest(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username"); // getting username in json for which we are sending request
        User sender = getAuthenticatedUser();  //current user

        User receiver = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (sender.getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You cannot send a friend request to yourself");
        } // check for not send request to myself

        Optional<FriendRequest> existingRequest = friendsRequestRepository.findTopBySenderAndReceiverOrderByIdDesc(sender,receiver);

        if (existingRequest.isPresent()) {
            FriendRequest friendRequest = existingRequest.get();

            //return status of request if it exists
            System.out.println("Existing friend request status: " + friendRequest.getStatus());

            //dividing situations to different exceptions
            if (friendRequest.getStatus() == RequestStatus.PENDING) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Friend request is already sent and is pending approval.");
            }
            if (friendRequest.getStatus() == RequestStatus.ACCEPTED) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Friend request is already sent and is pending approval.");
            }
        }


        userService.sendFriendsRequest(sender.getId(), username);  //sending request by current users id and receivers username
        return ResponseEntity.status(HttpStatus.CREATED).body("Friend request sent");
    }

    @GetMapping("/requests/from-me")  // get list of requests that i send to others
    public ResponseEntity<List<FriendRequestResponse>> getRequestsFromMe() {
        User authenticatedUser = getAuthenticatedUser();
        return ResponseEntity.ok(userService.getRequestFromMe(authenticatedUser.getId()));
    }

    @GetMapping("/requests/to-me") // get list of requests that was sent to me by others
    public ResponseEntity<List<FriendRequestResponse>> getRequestsToMe() {
        User authenticatedUser = getAuthenticatedUser();
        return ResponseEntity.ok(userService.getRequestsToMe(authenticatedUser.getId()));
    }

    @PostMapping("/requests/{requestId}/accept") // accepting specifical request sent to me by request's id
    public ResponseEntity<String> acceptFriendRequest(@PathVariable Long requestId) {
        userService.acceptFriendRequest(requestId);
        return ResponseEntity.ok("Friend request accepted");
    }

    @PostMapping("/requests/{requestId}/decline")// rejecting specifical request sent to me by request's id
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

    private User getAuthenticatedUser() {  // helper for authenticate check  and find them by email
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }
}

