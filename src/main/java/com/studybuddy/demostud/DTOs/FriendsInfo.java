package com.studybuddy.demostud.DTOs;

public class FriendsInfo {
    private Long id;
    private String username;
    private String avatarPath;

    public FriendsInfo(Long id, String username, String avatarPath) {
        this.id= id;
        this.username = username;
        this.avatarPath = avatarPath;
    }

    public String getAvatarPath() { return avatarPath; }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
