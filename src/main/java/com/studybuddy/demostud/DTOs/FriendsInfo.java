package com.studybuddy.demostud.DTOs;

public class FriendsInfo {
    private Long id;
    private String username;

    public FriendsInfo(Long id, String username) {
        this.id= id;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
