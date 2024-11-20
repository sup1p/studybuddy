package com.studybuddy.demostud.Service;


import com.studybuddy.demostud.enums.RequestStatus;
import com.studybuddy.demostud.models.FriendRequest;
import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.repository.FriendsRequestRepository;
import com.studybuddy.demostud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendsRequestRepository friendsRequestRepository;

    public List<User> getFriends(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getFriends();
    }

    public void sendFriendsRequest(Long senderId, String receiverUsername){
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findByUsername(receiverUsername)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (friendsRequestRepository.findBySenderAndReceiver(sender, receiver).isPresent()) {
            throw new RuntimeException("Friends request is already sent");
        }

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setStatus(RequestStatus.PENDING);
        friendsRequestRepository.save(friendRequest);
    }

    public List<FriendRequest> getRequestFromMe(Long userId) {
        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return friendsRequestRepository.findBySender(sender);
    }

    public List<FriendRequest> getRequestsToMe(Long userId) {
        User receiver = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return friendsRequestRepository.findByReceiver(receiver);
    }

    public void acceptFriendRequest(Long requestId) {
        FriendRequest request = friendsRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Friend request is not pending");
        }

        User sender = request.getSender();
        User receiver = request.getReceiver();

        sender.getFriends().add(receiver);
        receiver.getFriends().add(sender);

        request.setStatus(RequestStatus.ACCEPTED);

        userRepository.save(sender);
        userRepository.save(receiver);
        friendsRequestRepository.save(request);
    }

    public void declineFriendRequest(Long requestId) {
        FriendRequest request = friendsRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Friend request is not pending");
        }

        // Update request status to REJECTED
        request.setStatus(RequestStatus.REJECTED);
        friendsRequestRepository.save(request);
    }
}


