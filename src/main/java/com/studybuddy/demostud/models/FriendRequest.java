package com.studybuddy.demostud.models;

import com.studybuddy.demostud.enums.RequestStatus;
import jakarta.persistence.*;

@Entity
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @ManyToOne
    @JoinColumn(name = "sender_avatar_url")
    private User senderAvatarURL;

    @ManyToOne
    @JoinColumn(name = "receiver_avatar_url")
    private User receiverAvatarURL;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public User getSenderAvatarURL() {
        return senderAvatarURL;
    }

    public void setSenderAvatarURL(User senderAvatarURL) {
        this.senderAvatarURL = senderAvatarURL;
    }

    public User getReceiverAvatarURL() {
        return receiverAvatarURL;
    }

    public void setReceiverAvatarURL(User receiverAvatarURL) {
        this.receiverAvatarURL = receiverAvatarURL;
    }
}