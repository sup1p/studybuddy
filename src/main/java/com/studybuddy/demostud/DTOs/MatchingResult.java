
package com.studybuddy.demostud.DTOs;

public class MatchingResult {
    private Long MyId;
    private String BuddieHelpToMeSubjects;
    private String MyHelpToBuddieSubjects;
    private Long totalScore;
    private String BuddiesName;

    public MatchingResult(Long MyId, String BuddiesName,String BuddieHelpToMeSubjects ,String MyHelpToBuddieSubjects , Long totalScore) {
        this.MyId = MyId;
        this.BuddiesName = BuddiesName;
        this.BuddieHelpToMeSubjects = BuddieHelpToMeSubjects;
        this.MyHelpToBuddieSubjects = MyHelpToBuddieSubjects;
        this.totalScore = totalScore;
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
