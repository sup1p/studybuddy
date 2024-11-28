package com.studybuddy.demostud.controllers;

import com.studybuddy.demostud.models.Conversation;
import com.studybuddy.demostud.models.Message;
import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.repository.ChatRepository.MessageRepository;
import com.studybuddy.demostud.repository.ChatRepository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.studybuddy.demostud.DTOs.ChatMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ConversationRepository conversationRepository;

    @MessageMapping("/send") // Handle messages sent to "/app/send"
    public void handleMessage(@Payload ChatMessage chatMessage) {
        // Find the conversation
        Conversation conversation = conversationRepository.findById(chatMessage.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        // Save the message
        Message message = new Message();
        message.setSender(chatMessage.getSender());
        message.setContent(chatMessage.getContent());
        message.setTimestamp(LocalDateTime.now());
        message.setConversation(conversation);

        messageRepository.save(message);

        // Broadcast to all participants in the conversation
        for (User participant : conversation.getParticipants()) {
            String userDestination = "/topic/messages/" + participant.getId();
            simpMessagingTemplate.convertAndSend(userDestination, message);
        }
    }
}

