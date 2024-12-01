package com.studybuddy.demostud.Service;

import com.studybuddy.demostud.models.Language;
import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.repository.LanguageRepository;
import com.studybuddy.demostud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
public class LanguageService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LanguageRepository languageRepository;

    //method to return all Languages that user speaks
    public Set<Language> getLanguages(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getLanguages();
    }
    //add language by giving value to "languageName"
    public void addLanguageToUser(Long userId, String languageName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь с id " + userId + " не найден."));

        Language language = languageRepository.findByLanguageName(languageName)
                .orElseThrow(() -> new RuntimeException("Язык \"" + languageName + "\" не найден."));

        //throw exception if user already has that language
        if (!user.getLanguages().contains(language)) {
            user.getLanguages().add(language);
            userRepository.save(user);
        } else {
            throw new RuntimeException("Пользователь уже владеет этим языком.");
        }
    }
    //delete languages by giving value to "languageName"
    public void deleteLanguageFromUser(Long userId, String languageName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь с id " + userId + " не найден"));


        Language language = languageRepository.findByLanguageName(languageName)
                .orElseThrow(() -> new RuntimeException("Язык: " + languageName + " не найден"));

        //check if user has that language, if no throw exception
        boolean exists = user.getLanguages().stream()
                .anyMatch(lang -> lang.getLanguageName().equalsIgnoreCase(languageName));

        if (!exists) {
            throw new RuntimeException("У пользователя нет языка " + languageName);
        }
        //default case, just remove it
        user.getLanguages().removeIf(lang -> lang.getLanguageName().equalsIgnoreCase(languageName));
        userRepository.save(user);
    }
}