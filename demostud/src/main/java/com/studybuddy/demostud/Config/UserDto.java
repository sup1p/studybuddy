package com.studybuddy.demostud.Config;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public class UserDto
{
    private Long id;
    @NotEmpty
    private String Nickname;
    @NotEmpty(message = "Email should not be empty")
    @Email
    private String email;
    @NotEmpty(message = "Password should not be empty")
    private String password;

    public @NotEmpty(message = "Password should not be empty") String getPassword() {
        return password;
    }

    public void setPassword(@NotEmpty(message = "Password should not be empty") String password) {
        this.password = password;
    }

    public @NotEmpty(message = "Email should not be empty") @Email String getEmail() {
        return email;
    }

    public void setEmail(@NotEmpty(message = "Email should not be empty") @Email String email) {
        this.email = email;
    }

    public @NotEmpty String getNickname() {
        return Nickname;
    }

    public void setNickname(@NotEmpty String firstName) {
        this.Nickname = firstName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
