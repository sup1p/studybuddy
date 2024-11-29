package com.studybuddy.demostud.controllers;

import com.studybuddy.demostud.DTOs.UserSubDisciplineResponse;
import com.studybuddy.demostud.Service.LanguageService;
import com.studybuddy.demostud.models.Language;
import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.models.disciplines_package.SubDiscipline;
import com.studybuddy.demostud.models.disciplines_package.UserSubDiscipline;
import com.studybuddy.demostud.repository.DissciplineRepostory.SubDisciplineRepository;
import com.studybuddy.demostud.repository.DissciplineRepostory.UserSubDisciplineRepository;
import com.studybuddy.demostud.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/user/profile")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    private final LanguageService languageService;
    private final SubDisciplineRepository subDisciplineRepository;
    private final UserRepository userRepository;
    private final UserSubDisciplineRepository userSubDisciplineRepository;

    public ProfileController(LanguageService languageService, UserRepository userRepository,
                             UserSubDisciplineRepository userSubDisciplineRepository,
                             SubDisciplineRepository subDisciplineRepository) {
        this.languageService = languageService;
        this.userRepository = userRepository;
        this.userSubDisciplineRepository = userSubDisciplineRepository;
        this.subDisciplineRepository = subDisciplineRepository;
    }

    // Helper method to get the authenticated user
    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

//    // Projects
//    @GetMapping("/projects")
//    public ResponseEntity<List<String>> getUserProjects() {
//        List<String> projects = List.of("Project 1", "Project 2", "Project 3");
//        return ResponseEntity.ok(projects);
//    }

    // Nickname
    @GetMapping("/nickname")
    public ResponseEntity<String> getUserNickname() {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(user.getUsername());
    }

    // About
    @GetMapping(value = "/about", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getUserAbout() {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(user.getAbout());
    }

    @PutMapping("/about/edit")
    public ResponseEntity<String> updateUserAbout(@RequestBody Map<String, String> updatedAbout) {
        User user = getAuthenticatedUser();
        String about = updatedAbout.get("about");
        if (about == null || about.isBlank()) {
            return new ResponseEntity<>("Поле 'about' не может быть пустым.", HttpStatus.BAD_REQUEST);
        }

        user.setAbout(about);
        userRepository.save(user);
        return new ResponseEntity<>("Биография пользователя обновлена успешно.", HttpStatus.OK);
    }

    // Languages
    @GetMapping("/language")
    public ResponseEntity<Set<Language>> getLanguages() {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(languageService.getLanguages(user.getId()));
    }

    @PostMapping("/language/add")
    public ResponseEntity<String> addLanguageToUser(@RequestBody Language language) {
        User user = getAuthenticatedUser();
        try {
            languageService.addLanguageToUser(user.getId(), language.getLanguageName());
            return ResponseEntity.ok("Язык успешно добавлен пользователю!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка: " + e.getMessage());
        }
    }

    @DeleteMapping("/language/delete")
    public ResponseEntity<String> deleteLanguage(@RequestBody Map<String, String> requestBody) {
        User user = getAuthenticatedUser();
        try {
            String languageName = requestBody.get("languageName");
            if (languageName == null) {
                throw new IllegalArgumentException("Language name is missing");
            }
            languageService.deleteLanguageFromUser(user.getId(), languageName);
            return ResponseEntity.ok("Язык успешно удалён!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка: " + e.getMessage());
        }
    }

    // Academic Skills (Disciplines)
    @GetMapping("/discipline")
    public ResponseEntity<List<UserSubDisciplineResponse>> getSkills() {
        User user = getAuthenticatedUser();
        List<UserSubDiscipline> userSubDisciplines = userSubDisciplineRepository.findByUserId(user.getId());

        if (userSubDisciplines.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

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

    @PostMapping("/discipline/add")
    public ResponseEntity<String> addDisciplineToUser(@RequestBody Map<String, String> requestBody) {
        User user = getAuthenticatedUser();

        SubDiscipline subDiscipline = subDisciplineRepository.findById(Long.parseLong(requestBody.get("subDisciplineId")))
                .orElseThrow(() -> new RuntimeException("Sub-discipline not found"));

        if (userSubDisciplineRepository.findByUserIdAndSubDisciplineId(user.getId(),
                Long.parseLong(requestBody.get("subDisciplineId"))).isPresent()) {
            return ResponseEntity.ok("You already have that discipline, it would be better if you will just edit it");
        }

        String skillLevel = requestBody.get("skillLevel");
        UserSubDiscipline userSubDiscipline = new UserSubDiscipline();
        userSubDiscipline.setUser(user);
        userSubDiscipline.setSubDiscipline(subDiscipline);
        userSubDiscipline.setSkillLevel(skillLevel);

        userSubDisciplineRepository.save(userSubDiscipline);
        return ResponseEntity.ok("Sub-Discipline added successfully");
    }

    @PutMapping("/discipline/edit")
    public ResponseEntity<String> editUserDiscipline(
            @RequestParam Long subDisciplineId, @RequestBody Map<String, String> updatedSkillLevel) {
        User user = getAuthenticatedUser();

        UserSubDiscipline userSubDiscipline = userSubDisciplineRepository.findByUserIdAndSubDisciplineId(
                        user.getId(), subDisciplineId)
                .orElseThrow(() -> new RuntimeException("Sub-discipline not found for this user"));

        String skillLevel = updatedSkillLevel.get("skillLevel");
        if (skillLevel == null || skillLevel.isBlank()) {
            return new ResponseEntity<>("Поле 'skillLevel' не может быть пустым.", HttpStatus.BAD_REQUEST);
        }

        userSubDiscipline.setSkillLevel(skillLevel);
        userSubDisciplineRepository.save(userSubDiscipline);
        return new ResponseEntity<>("Способности пользователя обновлены успешно.", HttpStatus.OK);
    }

    @DeleteMapping("/discipline/delete")
    public ResponseEntity<String> deleteUserDiscipline(@RequestParam Long subDisciplineId) {
        User user = getAuthenticatedUser();

        List<UserSubDiscipline> userSubDiscipline = userSubDisciplineRepository.findAllByUserIdAndSubDisciplineId(
                user.getId(), subDisciplineId);

        userSubDisciplineRepository.deleteAll(userSubDiscipline);
        return ResponseEntity.ok("Sub-Discipline removed successfully");
    }
}


