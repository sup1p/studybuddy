package com.studybuddy.demostud.DTOs;

public class LanguagesResponse {
    private Long id;
    private String name;

    public LanguagesResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
