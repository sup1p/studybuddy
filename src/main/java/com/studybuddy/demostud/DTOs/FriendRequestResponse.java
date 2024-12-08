package com.studybuddy.demostud.DTOs;

public class FriendRequestResponse {
    private Long requestId;
    private Long senderId;
    private String senderUsername;
    private Long receiverId;
    private String receiverUsername;
    private String requestStatus;
    private String senderAvatarURL;
    private String receiverAvatarURL;

    public String getRequestStatus() {
        return requestStatus;
    }


    public FriendRequestResponse(Long requestId, Long senderId, String senderUsername, Long receiverId, String receiverUsername, String requestStatus, String senderAvatarURL, String receiverAvatarURL) {
        this.requestId = requestId;
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.receiverId = receiverId;
        this.receiverUsername = receiverUsername;
        this.requestStatus = requestStatus;
        this.senderAvatarURL = senderAvatarURL;
        this.receiverAvatarURL = receiverAvatarURL;
    }

    public Long getRequestId() {
        return requestId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public String getSenderAvatarURL() {
        return senderAvatarURL;
    }

    public String getReceiverAvatarURL() {
        return receiverAvatarURL;
    }
}

