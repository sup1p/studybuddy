package com.studybuddy.demostud.repository;

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
public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_languages WHERE user_id = :userId", nativeQuery = true)
    void deleteUserLanguagesByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_roles WHERE user_id = :userId", nativeQuery = true)
    void deleteByRolesUserId(@Param("userId") Long userId);

    @Modifying
    @Query(value = "DELETE FROM user_friends WHERE user_id = :userId OR friend_id = :userId", nativeQuery = true)
    void deleteUserFriendsByUserId(@Param("userId") Long userId);

}









