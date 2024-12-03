package com.studybuddy.demostud.Service;


import com.studybuddy.demostud.DTOs.FriendRequestResponse;
import com.studybuddy.demostud.DTOs.FriendsInfo;
import com.studybuddy.demostud.enums.RequestStatus;
import com.studybuddy.demostud.models.FriendRequest;
import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.repository.FriendsRequestRepository;
import com.studybuddy.demostud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendsRequestRepository friendsRequestRepository;

    //return list of users friends
    public List<FriendsInfo> getFriends(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //using stream API to make list Map
        return user.getFriends().stream()
                .map(friend -> new FriendsInfo(
                        friend.getId(),
                        friend.getUsername()
                ))
                .collect(Collectors.toList());
    }

    //deleting user's own friendRequests
    public void deleteFriendRequest(Long requestId, Long userId) {
        FriendRequest friendRequest = friendsRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        //check if user try to delete other's request
        if (!friendRequest.getSender().getId().equals(userId)) {
            throw new RuntimeException("You can only delete requests sent by you");
        }

        friendsRequestRepository.delete(friendRequest);
    }

    //sending friend request by username
    public void sendFriendsRequest(Long senderId, String receiverUsername) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findByUsername(receiverUsername)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        //saving and giving status pending
        FriendRequest newRequest = new FriendRequest();
        newRequest.setSender(sender);
        newRequest.setReceiver(receiver);
        newRequest.setStatus(RequestStatus.PENDING);
        friendsRequestRepository.save(newRequest);

        System.out.println("New friend request created with status: PENDING");
    }



    //return every request sent by user
    public List<FriendRequestResponse> getRequestFromMe(Long userId) {
        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<FriendRequest> friendRequests = friendsRequestRepository.findBySender(sender);

        //user stream api to Map every required value to list
        return friendRequests.stream()
                .filter(request -> request.getStatus() == RequestStatus.PENDING) //also show only requests that is pending
                .map(request -> new FriendRequestResponse(
                        request.getId(),
                        request.getSender().getId(),
                        request.getSender().getUsername(),
                        request.getReceiver().getId(),
                        request.getReceiver().getUsername(),
                        request.getStatus().name()
                ))
                .collect(Collectors.toList());
    }

    //return request sent to user
    public List<FriendRequestResponse> getRequestsToMe(Long userId) {
        User receiver = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<FriendRequest> friendRequests = friendsRequestRepository.findByReceiver(receiver);

        //use stream to map every value to list and access only requests with status PENDING
        return friendRequests.stream()
                .filter(request -> request.getStatus() == RequestStatus.PENDING)
                .map(request -> new FriendRequestResponse(
                        request.getId(),
                        request.getSender().getId(),
                        request.getSender().getUsername(),
                        request.getReceiver().getId(),
                        request.getReceiver().getUsername(),
                        request.getStatus().name()
                ))
                .collect(Collectors.toList());
    }

    //accepting request by requestId
    public void acceptFriendRequest(Long requestId) {
        FriendRequest request = friendsRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        //check if its already rejected or accepted
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Friend request is not pending");
        }

        //make two users friends with each other if accepted, and set status - ACCEPTED and save it
        User sender = request.getSender();
        User receiver = request.getReceiver();

        sender.getFriends().add(receiver);
        receiver.getFriends().add(sender);

        request.setStatus(RequestStatus.ACCEPTED);

        userRepository.save(sender);
        userRepository.save(receiver);
        friendsRequestRepository.save(request);
    }
    //decline friends request by requestId
    public void declineFriendRequest(Long requestId) {
        FriendRequest request = friendsRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        //check if its already rejected or accepted
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Friend request is not pending");
        }

        //set to request status - REJECTED, and save it
        request.setStatus(RequestStatus.REJECTED);
        friendsRequestRepository.save(request);
    }
    //deleting friend by userId and friendId
    public void deleteFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        //check if they are friends
        if (!user.getFriends().contains(friend)) {
            throw new RuntimeException("The users are not friends");
        }

        //delete from both sides, and save changes
        user.getFriends().removeIf(f -> f.getId().equals(friendId));
        friend.getFriends().removeIf(f -> f.getId().equals(userId));

        userRepository.save(user);
        userRepository.save(friend);
    }


}


