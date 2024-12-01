package com.studybuddy.demostud.repository.DissciplineRepostory;

import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.models.disciplines_package.UserSubDiscipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserSubDisciplineRepository extends JpaRepository<UserSubDiscipline,Long> {
    List<UserSubDiscipline> findByUserId(Long userId);

    Optional<UserSubDiscipline> findByUserIdAndSubDisciplineId(Long userId, Long subDisciplineId);

    List<UserSubDiscipline> findAllByUserIdAndSubDisciplineId(Long userId, Long subDisciplineId);

    @Query(value = """
    WITH user_weak_subjects AS (
        SELECT :userId AS user_id, sd.id AS sub_discipline_id
        FROM sub_discipline sd
        WHERE sd.name IN :weakSubjects -- Выбранные слабые предметы
    ),
    user_strong_subjects AS (
        SELECT usd.sub_discipline_id, usd.level
        FROM user_sub_discipline usd
        WHERE usd.user_id = :userId AND usd.level BETWEEN 6 AND 10 -- Сильные предметы пользователя
    ),
    match_scores AS (
        SELECT 
            usd1.user_id AS student_1_id,
            usd2.user_id AS student_2_id,
            sd.name AS sub_discipline_name,
            CASE
                WHEN usd1.level BETWEEN 1 AND 3 AND usd2.level BETWEEN 8 AND 10 THEN 15
                WHEN usd1.level BETWEEN 1 AND 3 AND usd2.level BETWEEN 6 AND 7 THEN 10
                WHEN usd1.level BETWEEN 4 AND 5 AND usd2.level BETWEEN 8 AND 10 THEN 10
                WHEN usd1.level BETWEEN 4 AND 5 AND usd2.level BETWEEN 6 AND 7 THEN 5
                ELSE 0
            END AS score,
            CASE
                WHEN usd1.user_id = :userId THEN 'student_1_help'
                WHEN usd2.user_id = :userId THEN 'student_2_help'
            END AS direction
        FROM user_sub_discipline usd1
        JOIN user_sub_discipline usd2
            ON usd1.sub_discipline_id = usd2.sub_discipline_id
        JOIN sub_discipline sd
            ON usd1.sub_discipline_id = sd.id
        WHERE (usd1.sub_discipline_id IN (SELECT sub_discipline_id FROM user_weak_subjects) 
               AND usd2.level BETWEEN 6 AND 10)
           OR (usd2.sub_discipline_id IN (SELECT sub_discipline_id FROM user_strong_subjects) 
               AND usd1.level BETWEEN 1 AND 5)
    )
    SELECT 
        student_1_id,
        student_2_id,
        MAX(CASE WHEN direction = 'student_1_help' THEN sub_discipline_name END) AS student_1_help_subjects,
        MAX(CASE WHEN direction = 'student_2_help' THEN sub_discipline_name END) AS student_2_help_subjects,
        SUM(score) AS total_score
    FROM match_scores
    WHERE score > 0
    GROUP BY student_1_id, student_2_id
    ORDER BY total_score DESC
""", nativeQuery = true)
    List<Object[]> findMatchesWithSelectedWeakSubjects(@Param("userId") Long userId, @Param("weakSubjects") List<String> weakSubjects);

    @Query(value = """
        WITH match_scores AS (
            SELECT
                usd1.user_id AS student_1_id,
                usd2.user_id AS student_2_id,
                sd.name AS sub_discipline_name,
                CASE
                    WHEN usd1.level BETWEEN 1 AND 3 AND usd2.level BETWEEN 4 AND 5 THEN 5
                    WHEN usd1.level BETWEEN 1 AND 3 AND usd2.level BETWEEN 6 AND 7 THEN 10
                    WHEN usd1.level BETWEEN 1 AND 3 AND usd2.level BETWEEN 8 AND 10 THEN 15
                    WHEN usd1.level BETWEEN 4 AND 5 AND usd2.level BETWEEN 6 AND 7 THEN 5
                    WHEN usd1.level BETWEEN 4 AND 5 AND usd2.level BETWEEN 8 AND 10 THEN 10
                    WHEN usd1.level BETWEEN 6 AND 7 AND usd2.level BETWEEN 8 AND 10 THEN 5
                    WHEN usd1.level BETWEEN 4 AND 5 AND usd2.level BETWEEN 1 AND 3 THEN 5
                    WHEN usd1.level BETWEEN 6 AND 7 AND usd2.level BETWEEN 1 AND 3 THEN 10
                    WHEN usd1.level BETWEEN 8 AND 10 AND usd2.level BETWEEN 1 AND 3 THEN 15
                    WHEN usd1.level BETWEEN 6 AND 7 AND usd2.level BETWEEN 4 AND 5 THEN 5
                    WHEN usd1.level BETWEEN 8 AND 10 AND usd2.level BETWEEN 4 AND 5 THEN 10
                    WHEN usd1.level BETWEEN 8 AND 10 AND usd2.level BETWEEN 6 AND 7 THEN 5
                    ELSE 0
                END AS score,
                CASE
                    WHEN usd1.level < usd2.level THEN 'student_1_help'
                    ELSE 'student_2_help'
                END AS direction
            FROM user_sub_discipline usd1
            JOIN user_sub_discipline usd2
                ON usd1.sub_discipline_id = usd2.sub_discipline_id
                AND usd1.user_id != usd2.user_id
            JOIN sub_discipline sd
                ON usd1.sub_discipline_id = sd.id
        )
        SELECT 
            student_1_id,
            student_2_id,
            MAX(CASE WHEN direction = 'student_1_help' THEN sub_discipline_name END) AS student_1_help_subjects,
            MAX(CASE WHEN direction = 'student_2_help' THEN sub_discipline_name END) AS student_2_help_subjects,
            SUM(score) AS total_score
        FROM match_scores
        WHERE score > 0
        GROUP BY student_1_id, student_2_id
        ORDER BY total_score DESC
    """, nativeQuery = true)
    List<Object[]> findMatchingPairsWithScores();
}
