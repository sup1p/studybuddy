package com.studybuddy.demostud.controllers;

import com.studybuddy.demostud.DTOs.MatchingResult;
import com.studybuddy.demostud.Service.MatchingService;
import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matching")
public class MatchingController {

    @Autowired
    private MatchingService matchingService;

    @Autowired
    private UserRepository userRepository;

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    @GetMapping("/default")
    public ResponseEntity<List<MatchingResult>> getDefaultRecommendations() {
        User user = getAuthenticatedUser(); // Получаем текущего пользователя
        List<MatchingResult> matches = matchingService.getDefaultRecommendations    (user.getId());
        return ResponseEntity.ok(matches);
    }

    @PostMapping("/search")
    public ResponseEntity<List<MatchingResult>> searchMatchingPairs(
            @RequestBody List<String> weakSubjects) {
        User user = getAuthenticatedUser(); // Получаем текущего пользователя
        List<MatchingResult> matches = matchingService.findMatchesWithWeakSubjects(user.getId(), weakSubjects);
        return ResponseEntity.ok(matches);
    }
}

