package com.studybuddy.demostud.repository;


import com.studybuddy.demostud.models.FriendRequest;
import com.studybuddy.demostud.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendsRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findBySender(User sender);
    List<FriendRequest> findByReceiver(User receiver);
    Optional<FriendRequest> findTopBySenderAndReceiverOrderByIdDesc(User sender, User receiver);
}
