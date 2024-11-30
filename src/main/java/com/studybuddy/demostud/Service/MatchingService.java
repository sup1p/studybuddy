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


        // Преобразуем результаты SQL-запроса в DTO
        return results.stream()
                .map(row -> new MatchingResult(
                        (Long) row[0], // student_1_id
                        (Long) row[1], // student_2_id
                        (String) row[2], // student_1_help_subjects
                        (String) row[3], // student_2_help_subjects
                        (Integer) row[4] // total_score
                ))
                .collect(Collectors.toList());
    }
    public List<MatchingResult> findMatchesWithWeakSubjects(Long userId, List<String> weakSubjects, String genderFilter, boolean locationFilter) {
        List<Object[]> results = userSubDisciplineRepository.findMatchesWithSelectedWeakSubjects(userId, weakSubjects);

        Optional<User> currentUser = Optional.ofNullable(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));

        String currentUserLocation = currentUser.get().getCountry();

        return results.stream()
                .filter(row -> {
                    // Проверка по гендеру
                    if (genderFilter != null && !genderFilter.isBlank()) {
                        Long student2Id = (Long) row[1]; // student_2_id
                        User student2 = userRepository.findById(student2Id)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                        if (!genderFilter.equalsIgnoreCase(student2.getGender())) {
                            return false; // Гендер не совпадает
                        }
                    }

                    // Проверка по локации
                    if (locationFilter) {
                        Long student2Id = (Long) row[1]; // student_2_id
                        User student2 = userRepository.findById(student2Id)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                        if (!currentUserLocation.equalsIgnoreCase(student2.getCountry())) {
                            return false; // Локация не совпадает
                        }
                    }

                    return true; // Если прошли все проверки
                })
                .map(row -> new MatchingResult(
                        (Long) row[0], // student_1_id
                        (Long) row[1], // student_2_id
                        (String) row[2], // student_1_help_subjects
                        (String) row[3], // student_2_help_subjects
                        (Integer) row[4] // total_score
                ))
                .collect(Collectors.toList());
    }
}
