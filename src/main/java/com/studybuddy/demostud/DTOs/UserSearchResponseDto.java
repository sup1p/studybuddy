package com.studybuddy.demostud.DTOs;

import com.studybuddy.demostud.models.disciplines_package.SubDiscipline;

import java.util.List;

public class UserSearchResponseDto {
    private final Long id;
    private final String username;
    private final String country;
    private final List<SubDisciplineWithSkillLevelDto> disciplines; // Обновлено
    private final String avatar;
    private final int matchingScore;

    public UserSearchResponseDto(Long id, String username, String country,
                                 List<SubDisciplineWithSkillLevelDto> disciplines,
                                 String avatar, int matchingScore) {
        this.id = id;
        this.username = username;
        this.country = country;
        this.disciplines = disciplines;
        this.avatar = avatar;
        this.matchingScore = matchingScore;
    }

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getCountry() {
        return country;
    }

    public List<SubDisciplineWithSkillLevelDto> getDisciplines() {
        return disciplines;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getMatchingScore() {
        return matchingScore;
    }
}

