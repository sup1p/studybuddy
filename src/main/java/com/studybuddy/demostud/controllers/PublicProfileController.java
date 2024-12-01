package com.studybuddy.demostud.controllers;

import com.studybuddy.demostud.Service.LanguageService;
import com.studybuddy.demostud.models.Language;
import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.models.disciplines_package.UserSubDiscipline;
import com.studybuddy.demostud.repository.DissciplineRepostory.UserSubDisciplineRepository;
import com.studybuddy.demostud.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.time.Period;
import java.time.LocalDate;

@RestController
@RequestMapping("/public/profile")
public class PublicProfileController {

    private final UserRepository userRepository;
    private final UserSubDisciplineRepository userSubDisciplineRepository;

    public PublicProfileController(UserRepository userRepository, UserSubDisciplineRepository userSubDisciplineRepository) {
        this.userRepository = userRepository;
        this.userSubDisciplineRepository = userSubDisciplineRepository;
    }

    // Getting base info about user, who found by id
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //counting his age
        int age = Period.between(user.getDateOfBirth(), LocalDate.now()).getYears();
        // Forming data with map
        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("age", age);
        response.put("languages", user.getLanguages());
        response.put("country", user.getCountry());
        response.put("numberOfFriends", user.getFriends().size());
        response.put("about", user.getAbout());
        response.put("male", user.getGender());

        return ResponseEntity.ok(response);
    }

    // Getting his disciplines
    @GetMapping("/{userId}/disciplines")
    public ResponseEntity<List<Map<String, Object>>> getUserDisciplines(@PathVariable Long userId) {
        List<UserSubDiscipline> userSubDisciplines = userSubDisciplineRepository.findByUserId(userId);

        if (userSubDisciplines.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        // Making all data to Map to answer
        List<Map<String, Object>> response = userSubDisciplines.stream()
                .map(userSubDiscipline -> {
                    Map<String, Object> discipline = new HashMap<>();
                    discipline.put("id", userSubDiscipline.getSubDiscipline().getId());
                    discipline.put("name", userSubDiscipline.getSubDiscipline().getName());
                    discipline.put("categoryName", userSubDiscipline.getSubDiscipline().getCategory().getName());
                    discipline.put("skillLevel", userSubDiscipline.getSkillLevel());
                    return discipline;
                })
                .toList();

        return ResponseEntity.ok(response);
    }
}


