package com.studybuddy.demostud.Service;

import com.studybuddy.demostud.DTOs.MatchingResult;
import com.studybuddy.demostud.repository.DissciplineRepostory.UserSubDisciplineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchingService {

    @Autowired
    private UserSubDisciplineRepository userSubDisciplineRepository;

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
    public List<MatchingResult> findMatchesWithWeakSubjects(Long userId, List<String> weakSubjects) {
        List<Object[]> results = userSubDisciplineRepository.findMatchesWithSelectedWeakSubjects(userId, weakSubjects);

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
}
