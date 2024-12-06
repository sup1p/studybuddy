
package com.studybuddy.demostud.DTOs;

public class MatchingResultDefault {
    private Long MyId;
    private Long BuddiesId;
    private String BuddiesName;
    private String MyHelpToBuddieSubjects;
    private String BuddieHelpToMeSubjects;
    private Long totalScore;

    public MatchingResultDefault(Long MyId, Long BuddiesId, String BuddiesName, String MyHelpToBuddieSubjects, String BuddieHelpToMeSubjects, Long totalScore) {
        this.MyId = MyId;
        this.BuddiesId = BuddiesId;
        this.BuddiesName = BuddiesName;
        this.MyHelpToBuddieSubjects = MyHelpToBuddieSubjects;
        this.BuddieHelpToMeSubjects = BuddieHelpToMeSubjects;
        this.totalScore = totalScore;
    }

    public Long getBuddiesId() {
        return BuddiesId;
    }

    public void setBuddiesId(Long buddiesId) {
        BuddiesId = buddiesId;
    }

    public String getBuddiesName() {
        return BuddiesName;
    }

    public void setBuddiesName(String buddiesName) {
        BuddiesName = buddiesName;
    }

    public Long getMyId() {
        return MyId;
    }

    public void setMyId(Long myId) {
        this.MyId = myId;
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
