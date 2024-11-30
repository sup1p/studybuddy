package com.studybuddy.demostud.DTOs;

public class MatchingResult {
    private Long student1Id;
    private Long student2Id;
    private String student1HelpSubjects;
    private String student2HelpSubjects;
    private Integer totalScore;

    public MatchingResult(Long student1Id, Long student2Id, String student1HelpSubjects, String student2HelpSubjects, Integer totalScore) {
        this.student1Id = student1Id;
        this.student2Id = student2Id;
        this.student1HelpSubjects = student1HelpSubjects;
        this.student2HelpSubjects = student2HelpSubjects;
        this.totalScore = totalScore;
    }

    public Long getStudent1Id() {
        return student1Id;
    }

    public void setStudent1Id(Long student1Id) {
        this.student1Id = student1Id;
    }

    public Long getStudent2Id() {
        return student2Id;
    }

    public void setStudent2Id(Long student2Id) {
        this.student2Id = student2Id;
    }

    public String getStudent1HelpSubjects() {
        return student1HelpSubjects;
    }

    public void setStudent1HelpSubjects(String student1HelpSubjects) {
        this.student1HelpSubjects = student1HelpSubjects;
    }

    public String getStudent2HelpSubjects() {
        return student2HelpSubjects;
    }

    public void setStudent2HelpSubjects(String student2HelpSubjects) {
        this.student2HelpSubjects = student2HelpSubjects;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }
}

