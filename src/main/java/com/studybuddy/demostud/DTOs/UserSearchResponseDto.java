package com.studybuddy.demostud.DTOs;

import com.studybuddy.demostud.models.disciplines_package.SubDiscipline;

import java.util.List;

public class UserSearchResponseDto {
    private Long id;
    private String username;
    private String country;
    private List<SubDiscipline> disciplines;
    private String avatar;

    // Конструктор
    public UserSearchResponseDto(Long id, String username, String country, List<SubDiscipline> disciplines, String avatar) {
        this.id = id;
        this.username = username;
        this.country = country;
        this.disciplines = disciplines;
        this.avatar = avatar;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<SubDiscipline> getDisciplines() {
        return disciplines;
    }

    public void setDisciplines(List<SubDiscipline> disciplines) {
        this.disciplines = disciplines;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}

