package com.studybuddy.demostud.DTOs;

import com.studybuddy.demostud.models.disciplines_package.SubDiscipline;

public class SubDisciplineWithSkillLevelDto {
    private final SubDiscipline subDiscipline;
    private final int skillLevel;

    public SubDisciplineWithSkillLevelDto(SubDiscipline subDiscipline, int skillLevel) {
        this.subDiscipline = subDiscipline;
        this.skillLevel = skillLevel;
    }

    public SubDiscipline getSubDiscipline() {
        return subDiscipline;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

}
