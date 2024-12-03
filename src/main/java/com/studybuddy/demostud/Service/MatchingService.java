package com.studybuddy.demostud.Service;

import com.studybuddy.demostud.DTOs.MatchingResult;
import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.repository.DissciplineRepostory.UserSubDisciplineRepository;
import com.studybuddy.demostud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MatchingService {

    @Autowired
    private UserSubDisciplineRepository userSubDisciplineRepository;

    @Autowired
    private UserRepository userRepository;

    public List<MatchingResult> getDefaultRecommendations(Long userId) {
        List<Object[]> results = userSubDisciplineRepository.findMatchingPairsWithScores();

        // Transform result of SQL-request into DTO
        return results.stream()
                .map(row -> new MatchingResult(
                        (Long) row[0], // matching_user_id
                        (Long) row[1], // buddies_id
                        (String) row[2], // buddies_username
                        (String) row[3], // help_provided_subjects
                        (String) row[4], // help_needed_subjects
                        (Long) row[5]  // total_score
                ))
                .collect(Collectors.toList());
    }
    public List<MatchingResult> findMatchesWithWeakSubjects(Long userId, List<String> weakSubjects, String genderFilter, boolean locationFilter) {
        List<Object[]> results = userSubDisciplineRepository.findMatchesWithSelectedWeakSubjects(userId, weakSubjects);

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String currentUserLocation = currentUser.getCountry();

        return results.stream()
                .filter(row -> {
                    // Gender filter
                    if (genderFilter != null && !genderFilter.isBlank()) {
                        String matchingUserUsername = (String) row[1]; // matching_user_username
                        User matchingUser = userRepository.findByUsername(matchingUserUsername)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                        if (!genderFilter.equalsIgnoreCase(matchingUser.getGender())) {
                            return false; // gender doesn't match
                        }
                    }

                    // Location filter
                    if (locationFilter) {
                        String matchingUserUsername = (String) row[1]; // matching_user_username
                        User matchingUser = userRepository.findByUsername(matchingUserUsername)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                        if (!currentUserLocation.equalsIgnoreCase(matchingUser.getCountry())) {
                            return false; // location doesn't match
                        }
                    }

                    return true; // all filters passed
                })
                .map(row -> new MatchingResult(
                        (Long) row[0], // matching_user_id
                        (Long) row[1], // buddies_id
                        (String) row[2], // buddies_username
                        (String) row[3], // help_provided_subjects
                        (String) row[4], // help_needed_subjects
                        (Long) row[5]  // total_score
                ))
                .collect(Collectors.toList());
    }
}
