package com.studybuddy.demostud.controllers;

import com.studybuddy.demostud.DTOs.MatchingResultDefault;
import com.studybuddy.demostud.DTOs.SearchRequest;
import com.studybuddy.demostud.DTOs.UserSearchResponseDto;
import com.studybuddy.demostud.Service.MatchingService;
import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.models.disciplines_package.SubDiscipline;
import com.studybuddy.demostud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<MatchingResultDefault>> getDefaultRecommendations() {
        User user = getAuthenticatedUser(); // current user
        List<MatchingResultDefault> matches = matchingService.getDefaultRecommendations(user.getId());

        if (user.getId() != null) {
            matches = matches.stream()
                    .filter(matchingResultDefault -> matchingResultDefault.getMyId().equals(user.getId()))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(matches);
    }

    @PostMapping("/search")   // searching for buddies by specific disciplines that replace user's weaknesses
    public ResponseEntity<List<UserSearchResponseDto>> searchMatchingPairs(@RequestBody SearchRequest searchRequest) {
        User currentUser = getAuthenticatedUser(); // get current authenticated user
        List<String> weakSubjects = searchRequest.getWeakSubjects();
        String genderFilter = searchRequest.getGenderFilter();
        boolean locationFilter = searchRequest.isLocationFilter();
        List<User> matchingUsers = matchingService.findMatchingUsers(currentUser, weakSubjects, genderFilter, locationFilter);

        List<UserSearchResponseDto> response = matchingUsers.stream()
                .map(user -> {
                    // Получаем дисциплины пользователя из user_sub_discipline
                    List<SubDiscipline> disciplines = MatchingService.findSubjectsByUserId(user.getId());

                    // Создаем DTO
                    return new UserSearchResponseDto(
                            user.getId(),
                            user.getUsername(),
                            user.getCountry(),
                            disciplines,
                            user.getAvatarPath()
                    );
                })
                .toList();

        return ResponseEntity.ok(response);
    }
}

