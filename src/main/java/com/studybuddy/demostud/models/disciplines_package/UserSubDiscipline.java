package com.studybuddy.demostud.models.disciplines_package;


import com.studybuddy.demostud.models.User;
import jakarta.persistence.*;

@Entity
@Table(name = "user_sub_discipline")
public class UserSubDiscipline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "sub_discipline_id")
    private SubDiscipline subDiscipline;

    private String skillLevel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SubDiscipline getSubDiscipline() {
        return subDiscipline;
    }

    public void setSubDiscipline(SubDiscipline subDiscipline) {
        this.subDiscipline = subDiscipline;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }
}
