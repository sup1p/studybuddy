package com.studybuddy.demostud.repository.ChatRepository;

import com.studybuddy.demostud.models.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
}
