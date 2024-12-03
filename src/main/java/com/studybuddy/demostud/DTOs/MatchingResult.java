
package com.studybuddy.demostud.DTOs;

public class MatchingResult {
    private Long MyId;
    private Long BuddiesId;
    private String MyHelpToBuddieSubjects;
    private String BuddieHelpToMeSubjects;
    private Long totalScore;

    public MatchingResult(Long MyId, Long BuddiesId, String MyHelpToBuddieSubjects, String BuddieHelpToMeSubjects, Long totalScore) {
        this.MyId = MyId;
        this.BuddiesId = BuddiesId;
        this.MyHelpToBuddieSubjects = MyHelpToBuddieSubjects;
        this.BuddieHelpToMeSubjects = BuddieHelpToMeSubjects;
        this.totalScore = totalScore;
    }

    public Long getMyId() {
        return MyId;
    }

    public void setMyId(Long myId) {
        this.MyId = myId;
    }

    public Long getBuddiesId() {
        return BuddiesId;
    }

    public void setBuddiesId(Long buddiesId) {
        this.BuddiesId = buddiesId;
    }

    public String getMyHelpToBuddieSubjects() {
        return MyHelpToBuddieSubjects;
    }

    public void setMyHelpToBuddieSubjects(String myHelpToBuddieSubjects) {
        this.MyHelpToBuddieSubjects = myHelpToBuddieSubjects;
    }

    public String getBuddieHelpToMeSubjects() {
        return BuddieHelpToMeSubjects;
    }

    public void setBuddieHelpToMeSubjects(String buddieHelpToMeSubjects) {
        this.BuddieHelpToMeSubjects = buddieHelpToMeSubjects;
    }

    public Long getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Long totalScore) {
        this.totalScore = totalScore;
    }
}
