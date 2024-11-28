package com.studybuddy.demostud.repository.ChatRepository;

import com.studybuddy.demostud.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
