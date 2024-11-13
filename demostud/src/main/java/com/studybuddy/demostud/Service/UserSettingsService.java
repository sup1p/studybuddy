package com.studybuddy.demostud.Service;

import com.studybuddy.demostud.models.UserSettings;
import com.studybuddy.demostud.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserSettingsService {

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    public UserSettingsService(UserSettingsRepository userSettingsRepository) {
        this.userSettingsRepository = userSettingsRepository;
    }

    public Optional<UserSettings> getUserSettings(Long id) {
        return userSettingsRepository.findById(id);
    }

    public UserSettings saveUserSettings(UserSettings settings) {
        return userSettingsRepository.save(settings);
    }
}