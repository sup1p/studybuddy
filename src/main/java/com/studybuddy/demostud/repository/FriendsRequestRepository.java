package com.studybuddy.demostud.repository;


import com.studybuddy.demostud.models.FriendRequest;
import com.studybuddy.demostud.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendsRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findBySender(User sender);
    List<FriendRequest> findByReceiver(User receiver);
    Optional<FriendRequest> findTopBySenderAndReceiverOrderByIdDesc(User sender, User receiver);

    @Modifying
    @Transactional
    @Query("DELETE FROM FriendRequest fr WHERE fr.sender.id = :userId OR fr.receiver.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}

