package com.studybuddy.demostud.models.disciplines_package;

import com.studybuddy.demostud.models.User;

public class MatchingUser {
    private User user;
    private int matchingScore;

    public MatchingUser(User user, int matchingScore) {
        this.user = user;
        this.matchingScore = matchingScore;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getMatchingScore() {
        return matchingScore;
    }

    public void setMatchingScore(int matchingScore) {
        this.matchingScore = matchingScore;
    }
}
