package com.studybuddy.demostud.DTOs;

public class FriendRequestResponse {
    private Long requestId;
    private Long senderId;
    private String senderUsername;
    private Long receiverId;
    private String receiverUsername;
    private String requestStatus;

    public String getRequestStatus() {
        return requestStatus;
    }


    public FriendRequestResponse(Long requestId, Long senderId, String senderUsername, Long receiverId, String receiverUsername, String requestStatus) {
        this.requestId = requestId;
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.receiverId = receiverId;
        this.receiverUsername = receiverUsername;
        this.requestStatus = requestStatus;
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
}

