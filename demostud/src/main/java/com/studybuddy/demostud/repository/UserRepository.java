package com.studybuddy.demostud.repository;

import com.studybuddy.demostud.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    @Query("SELECT u.username FROM User u WHERE u.id = :id")
    String findUsernameById(Long id);
}


