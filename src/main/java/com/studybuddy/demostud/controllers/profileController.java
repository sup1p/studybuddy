package com.studybuddy.demostud.controllers;

import com.studybuddy.demostud.DTOs.UserSubDisciplineResponse;
import com.studybuddy.demostud.Service.LanguageService;
import com.studybuddy.demostud.Service.UserService;
import com.studybuddy.demostud.models.Language;
import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.models.disciplines_package.SubDiscipline;
import com.studybuddy.demostud.models.disciplines_package.UserSubDiscipline;
import com.studybuddy.demostud.repository.DissciplineRepostory.SubDisciplineRepository;
import com.studybuddy.demostud.repository.DissciplineRepostory.UserSubDisciplineRepository;
import com.studybuddy.demostud.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/profile")
public class profileController {

    private static final Logger logger = LoggerFactory.getLogger(mainController.class);

    @Autowired
    private LanguageService languageService;

    private final SubDisciplineRepository subDisciplineRepository;
    private final UserRepository userRepository;
    private final UserSubDisciplineRepository userSubDisciplineRepository;

    public profileController(UserRepository userRepository, UserSubDisciplineRepository userSubDisciplineRepository, SubDisciplineRepository subDisciplineRepository) {
        this.userRepository = userRepository;
        this.userSubDisciplineRepository = userSubDisciplineRepository;
        this.subDisciplineRepository = subDisciplineRepository;
    }


    // Эндпоинт для проектов
    @GetMapping("/{userId}/projects")
    public ResponseEntity<List<String>> getUserProjects(@PathVariable Long userId) {
        // Замените на реальную логику получения данных
        List<String> projects = List.of("Project 1", "Project 2", "Project 3");
        return ResponseEntity.ok(projects);
    }

    //NICKNAME
    @GetMapping("/{userId}/nickname")
    public ResponseEntity<String> getUserNickname(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user.getUsername());
    }

    //ABOUT
    @GetMapping(value = "/{userId}/about", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getUserAbout(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user.getAbout());
    }


    @PutMapping("/{userId}/about/edit")
    public ResponseEntity<String> updateUserAbout(
            @PathVariable Long userId,
            @RequestBody Map<String, String> updatedAbout) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        String about = updatedAbout.get("about");
        if (about == null || about.isBlank()) {
            return new ResponseEntity<>("Поле 'about' не может быть пустым.", HttpStatus.BAD_REQUEST);
        }

        // Обновляем и сохраняем
        user.setAbout(about);
        userRepository.save(user);

        return new ResponseEntity<>("Биография пользователя обновлена успешно.", HttpStatus.OK);
    }

    //LANGUAGE
    @GetMapping("/{userId}/language")
    public ResponseEntity<Set<Language>> getLanguages(@PathVariable Long userId) {
        return ResponseEntity.ok(languageService.getLanguages(userId));
    }

    @PostMapping("/{userId}/language/add")
    public ResponseEntity<String> addLanguageToUser(@PathVariable Long userId, @RequestBody Language language) {
        try {
            languageService.addLanguageToUser(userId, language.getLanguageName());
            return ResponseEntity.ok("Язык успешно добавлен пользователю!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка: " + e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/language/delete")
    public ResponseEntity<String> deleteLanguage(@PathVariable Long userId, @RequestBody Map<String, String> requestBody) {
        try {
            String languageName = requestBody.get("languageName");
            if (languageName == null) {
                throw new IllegalArgumentException("Language name is missing");
            }
            languageService.deleteLanguageFromUser(userId, languageName);
            return ResponseEntity.ok("Язык успешно удалён!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка: " + e.getMessage());
        }
    }

    //ACADEMIC_SKILLS
    @GetMapping("/{userId}/discipline")
    public ResponseEntity<List<UserSubDisciplineResponse>> getSkills(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<UserSubDiscipline> userSubDisciplines = userSubDisciplineRepository.findByUserId(userId);

        if (userSubDisciplines.isEmpty())
            return ResponseEntity.ok(Collections.emptyList());

        List<UserSubDisciplineResponse> response = userSubDisciplines.stream()
                .map(userSubDiscipline -> new UserSubDisciplineResponse(
                        userSubDiscipline.getSubDiscipline().getId(),
                        userSubDiscipline.getSubDiscipline().getName(),
                        userSubDiscipline.getSubDiscipline().getCategory().getName(),
                        userSubDiscipline.getSkillLevel()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
    @PostMapping("/{userId}/discipline/add")
    public ResponseEntity<String> addDisciplineToUser(
            @PathVariable Long userId, @RequestBody Map<String,String> requestBody) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SubDiscipline subDiscipline = subDisciplineRepository.findById(Long.parseLong(requestBody.get("subDisciplineId")))
                .orElseThrow(() -> new RuntimeException("Sub-discipline not found"));


        if(userSubDisciplineRepository.findByUserIdAndSubDisciplineId(userId, Long.valueOf(requestBody.get("subDisciplineId"))).isPresent())
            return ResponseEntity.ok("You already have that discipline, it would be better if you will just edit it");

        String skillLevel = requestBody.get("skillLevel");
        UserSubDiscipline userSubDiscipline = new UserSubDiscipline();
        userSubDiscipline.setUser(user);
        userSubDiscipline.setSubDiscipline(subDiscipline);
        userSubDiscipline.setSkillLevel(skillLevel);

        userSubDisciplineRepository.save(userSubDiscipline);
        return ResponseEntity.ok("Sub-Discipline added successfully");
    }

    @PutMapping("/{userId}/discipline/edit")
    public ResponseEntity<String> editUserDiscipline(
            @PathVariable Long userId, @RequestParam Long subDisciplineId, @RequestBody Map<String,String> updatedSkillLevel) {
    UserSubDiscipline userSubDiscipline = userSubDisciplineRepository.findByUserIdAndSubDisciplineId(userId,subDisciplineId)
            .orElseThrow(() -> new RuntimeException("Sub-discipline not found for this user"));

        String skillLevel = updatedSkillLevel.get("skillLevel");
        if (skillLevel == null || skillLevel.isBlank()) {
            return new ResponseEntity<>("Поле 'skillLevel' не может быть пустым.", HttpStatus.BAD_REQUEST);
        }

        userSubDiscipline.setSkillLevel(skillLevel);
        userSubDisciplineRepository.save(userSubDiscipline);

        return new ResponseEntity<>("Способности пользователя обновлены успешно.", HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/discipline/delete")
    public ResponseEntity<String> deleteUserDiscipline(
            @PathVariable Long userId, @RequestParam Long subDisciplineId) {
        List<UserSubDiscipline> userSubDiscipline = userSubDisciplineRepository.findAllByUserIdAndSubDisciplineId(userId, subDisciplineId);


        userSubDisciplineRepository.deleteAll(userSubDiscipline);
        return ResponseEntity.ok("Sub-Discipline removed successfully");
    }

}

