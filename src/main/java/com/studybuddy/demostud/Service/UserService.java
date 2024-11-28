package com.studybuddy.demostud.Service;


import com.studybuddy.demostud.enums.RequestStatus;
import com.studybuddy.demostud.models.FriendRequest;
import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.repository.FriendsRequestRepository;
import com.studybuddy.demostud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendsRequestRepository friendsRequestRepository;

    public Set<User> getFriends(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getFriends();
    }

    public void deleteFriendRequest(Long requestId, Long userId) {
        FriendRequest friendRequest = friendsRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        if (!friendRequest.getSender().getId().equals(userId)) {
            throw new RuntimeException("You can only delete requests sent by you");
        }

        friendsRequestRepository.delete(friendRequest);
    }

    public void sendFriendsRequest(Long senderId, String receiverUsername) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findByUsername(receiverUsername)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        List<RequestStatus> blockingStatuses = Arrays.asList(RequestStatus.PENDING, RequestStatus.ACCEPTED);


        Optional<FriendRequest> existingRequest = friendsRequestRepository.findTopBySenderAndReceiverOrderByIdDesc(sender,receiver);

        if (existingRequest.isPresent()) {
            FriendRequest friendRequest = existingRequest.get();

            System.out.println("Existing friend request status: " + friendRequest.getStatus());

            if (friendRequest.getStatus() == RequestStatus.PENDING) {
                throw new RuntimeException("Friend request is already sent and is pending approval.");
            }
            if (friendRequest.getStatus() == RequestStatus.ACCEPTED) {
                throw new RuntimeException("Friend request is already sent and has been accepted.");
            }
        }

        FriendRequest newRequest = new FriendRequest();
        newRequest.setSender(sender);
        newRequest.setReceiver(receiver);
        newRequest.setStatus(RequestStatus.PENDING);
        friendsRequestRepository.save(newRequest);

        System.out.println("New friend request created with status: PENDING");
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

        request.setStatus(RequestStatus.REJECTED);
        friendsRequestRepository.save(request);
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        if (!user.getFriends().contains(friend)) {
            throw new RuntimeException("The users are not friends");
        }


        user.getFriends().removeIf(f -> f.getId().equals(friendId));
        friend.getFriends().removeIf(f -> f.getId().equals(userId));

        userRepository.save(user);
        userRepository.save(friend);
    }


}


