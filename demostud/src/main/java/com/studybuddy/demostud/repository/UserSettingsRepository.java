package com.studybuddy.demostud.repository;

import com.studybuddy.demostud.models.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    UserSettings findByUserUsername(String username);
}