package com.studybuddy.demostud.DTOs;

import java.util.List;

public class SearchRequest {
    private List<String> weakSubjects;
    private String genderFilter;
    private boolean locationFilter;

    public List<String> getWeakSubjects() {
        return weakSubjects;
    }

    public void setWeakSubjects(List<String> weakSubjects) {
        this.weakSubjects = weakSubjects;
    }

    public String getGenderFilter() {
        return genderFilter;
    }

    public void setGenderFilter(String genderFilter) {
        this.genderFilter = genderFilter;
    }

    public boolean isLocationFilter() {
        return locationFilter;
    }

    public void setLocationFilter(boolean locationFilter) {
        this.locationFilter = locationFilter;
    }
}
