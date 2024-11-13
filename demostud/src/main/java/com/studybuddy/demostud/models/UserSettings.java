package com.studybuddy.demostud.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name  = "UserSettings")
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")  // Внешний ключ user_id ссылается на id в таблице User
    private User user;

    public String getLinkedUsername() {
        return user != null ? user.getUsername() : null;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    private String language;


    private LocalDate dateOfBirth;


    private String country;

    // Getters and Setters


    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
