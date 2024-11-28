package com.studybuddy.demostud.DTOs;

public class UserSubDisciplineResponse {
    private Long subDisciplineId;
    private String subDisciplineName;
    private String category;
    private String skillLevel;

    public UserSubDisciplineResponse(Long subDisciplineId, String subDisciplineName, String category, String skillLevel) {
        this.subDisciplineId = subDisciplineId;
        this.subDisciplineName = subDisciplineName;
        this.category = category;
        this.skillLevel = skillLevel;
    }

    public Long getSubDisciplineId() {
        return subDisciplineId;
    }

    public void setSubDisciplineId(Long subDisciplineId) {
        this.subDisciplineId = subDisciplineId;
    }

    public String getSubDisciplineName() {
        return subDisciplineName;
    }

    public void setSubDisciplineName(String subDisciplineName) {
        this.subDisciplineName = subDisciplineName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }
}
