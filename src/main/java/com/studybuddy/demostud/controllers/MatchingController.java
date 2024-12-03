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
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/user/matching")
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

        if (user.getId() != null) {
            matches = matches.stream()
                    .filter(matchingResult -> matchingResult.getMyId().equals(user.getId()))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(matches);
    }

    @PostMapping("/search")   // searching for buddies by specific disciplines that replace user's weaknesses
    public ResponseEntity<List<MatchingResult>> searchMatchingPairs(@RequestBody SearchRequest searchRequest) {
        User user = getAuthenticatedUser(); // get current authenticated user
        Logger log = LoggerFactory.getLogger(this.getClass());


        boolean locationFilter = searchRequest.isLocationFilter(); // turn on/off location filter
        String genderFilter = searchRequest.getGenderFilter(); // get gender filter (e.g., male, female, doesn't matter)

        log.info("User ID: {}", user.getId());
        log.info("Weak Subjects: {}", searchRequest.getWeakSubjects());
        log.info("Gender Filter: {}", genderFilter);
        log.info("Location Filter: {}", locationFilter);

        List<MatchingResult> matches = matchingService.findMatchesWithWeakSubjects(
                user.getId(),
                searchRequest.getWeakSubjects(),
                genderFilter,
                locationFilter
        );

        log.info("Matches found: {}", matches.size());

        // No need to filter by user ID here, as matchingService already returns the correct results
        return ResponseEntity.ok(matches);
    }

}

