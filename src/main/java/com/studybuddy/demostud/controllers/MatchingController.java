package com.studybuddy.demostud.controllers;

import com.studybuddy.demostud.DTOs.MatchingResult;
import com.studybuddy.demostud.DTOs.SearchRequest;
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

    private User getAuthenticatedUser() { //helper for identify
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    @GetMapping("/default")  //getting recommendations of possible buddies
    public ResponseEntity<List<MatchingResult>> getDefaultRecommendations() {
        User user = getAuthenticatedUser(); // current user
        List<MatchingResult> matches = matchingService.getDefaultRecommendations(user.getId());
        return ResponseEntity.ok(matches);
    }

    @PostMapping("/search")   //searching to buddies by specific disciplines, that replaces user's weaknesses
    public ResponseEntity<List<MatchingResult>> searchMatchingPairs(
            @RequestBody SearchRequest searchRequest) {
        User user = getAuthenticatedUser(); // current user

        boolean locationFilter = searchRequest.isLocationFilter();  //turn on/of location filter
        String genderFilter = searchRequest.getGenderFilter();   // getting gender filter that then finds only male/female/doesnt matter

        List<MatchingResult> matches = matchingService.findMatchesWithWeakSubjects(user.getId(),
                searchRequest.getWeakSubjects(),
                genderFilter,locationFilter);

        return ResponseEntity.ok(matches);
    }
}
