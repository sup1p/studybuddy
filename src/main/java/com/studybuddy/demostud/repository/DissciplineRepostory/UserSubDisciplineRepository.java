package com.studybuddy.demostud.repository.DissciplineRepostory;

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
        WITH match_scores AS (
            SELECT
                usd1.user_id AS student_1_id,
                usd2.user_id AS student_2_id,
                u2.username AS student_2_username,
                sd.name AS sub_discipline_name,
                CASE
                    WHEN usd1.skill_level BETWEEN 1 AND 3 AND usd2.skill_level BETWEEN 4 AND 5 THEN 5
                    WHEN usd1.skill_level BETWEEN 1 AND 3 AND usd2.skill_level BETWEEN 6 AND 7 THEN 10
                    WHEN usd1.skill_level BETWEEN 4 AND 5 AND usd2.skill_level BETWEEN 6 AND 7 THEN 5
                    WHEN usd1.skill_level BETWEEN 4 AND 5 AND usd2.skill_level BETWEEN 8 AND 10 THEN 10
                    WHEN usd1.skill_level BETWEEN 1 AND 3 AND usd2.skill_level BETWEEN 8 AND 10 THEN 15
                    WHEN usd1.skill_level BETWEEN 6 AND 7 AND usd2.skill_level BETWEEN 8 AND 10 THEN 5
                    WHEN usd1.skill_level BETWEEN 4 AND 5 AND usd2.skill_level BETWEEN 1 AND 3 THEN 5
                    WHEN usd1.skill_level BETWEEN 6 AND 7 AND usd2.skill_level BETWEEN 1 AND 3 THEN 10
                    WHEN usd1.skill_level BETWEEN 8 AND 10 AND usd2.skill_level BETWEEN 1 AND 3 THEN 15
                    WHEN usd1.skill_level BETWEEN 6 AND 7 AND usd2.skill_level BETWEEN 4 AND 5 THEN 5
                    WHEN usd1.skill_level BETWEEN 8 AND 10 AND usd2.skill_level BETWEEN 4 AND 5 THEN 10
                    WHEN usd1.skill_level BETWEEN 8 AND 10 AND usd2.skill_level BETWEEN 6 AND 7 THEN 5  -- просмотр всех случаев как студенты могут быть друг другу полезны
                    ELSE 0
                END AS score,
                CASE
                    WHEN usd1.skill_level < usd2.skill_level THEN 'student_1_help'
                    ELSE 'student_2_help'
                END AS direction  --определяет какой студент помогает другому в этом предмете
            FROM user_sub_discipline usd1
            JOIN user_sub_discipline usd2
                ON usd1.sub_discipline_id = usd2.sub_discipline_id --поиск одной дисциплины на двоих
                AND usd1.user_id != usd2.user_id
            JOIN sub_discipline sd
                ON usd1.sub_discipline_id = sd.id
            JOIN users u2
                ON usd2.user_id = u2.id
        )
        SELECT
            student_1_id,
            student_2_id,
            student_2_username,
            MAX(CASE WHEN direction = 'student_1_help' THEN sub_discipline_name END) AS student_1_help_subjects,
            MAX(CASE WHEN direction = 'student_2_help' THEN sub_discipline_name END) AS student_2_help_subjects,
            SUM(score) AS total_score --вычисляется общая сумма уровня взаимопомощи
        FROM match_scores
        WHERE score > 0
        GROUP BY student_1_id, student_2_id, student_2_username
        ORDER BY total_score DESC
    """, nativeQuery = true)
        //рекомендации, лишь возвращает студентов на основе заранее определенных на аккаунте слабых дисциплин
    List<Object[]> findMatchingPairsWithScores();



    @Query(value = """
        WITH user_weak_subjects AS (
            SELECT id AS sub_discipline_id
            FROM sub_discipline
            WHERE name IN (:weakSubjectNames)
        ),
        user_strong_subjects AS (
            SELECT sub_discipline_id
            FROM user_sub_discipline
            WHERE user_id = :userId
              AND skill_level BETWEEN 6 AND 10
        ),
        match_scores AS (
            SELECT
                :userId AS my_id,
                usd2.user_id AS matching_user_id,
                u2.username AS matching_user_username,
                sd.name AS sub_discipline_name,
                CASE
                    -- Другой пользователь силен в слабых предметах текущего пользователя
                    WHEN uws.sub_discipline_id = usd2.sub_discipline_id AND usd2.skill_level BETWEEN 8 AND 10 THEN 15
                    WHEN uws.sub_discipline_id = usd2.sub_discipline_id AND usd2.skill_level BETWEEN 6 AND 7 THEN 10
                    -- Текущий пользователь силен в слабых предметах другого пользователя
                    WHEN usd2.sub_discipline_id IN (SELECT sub_discipline_id FROM user_strong_subjects) AND usd2.skill_level BETWEEN 1 AND 5 THEN 7
                    ELSE 0
                END AS score,
                CASE
                    -- Другой пользователь помогает текущему пользователю
                    WHEN uws.sub_discipline_id = usd2.sub_discipline_id AND usd2.skill_level BETWEEN 8 AND 10 THEN 'user_help'
                    WHEN uws.sub_discipline_id = usd2.sub_discipline_id AND usd2.skill_level BETWEEN 6 AND 7 THEN 'user_help'                    
                    -- Текущий пользователь помогает другому пользователю
                    WHEN usd2.sub_discipline_id IN (SELECT sub_discipline_id FROM user_strong_subjects) AND usd2.skill_level BETWEEN 1 AND 5 THEN 'matching_user_help'
                    ELSE NULL
                END AS direction
            FROM user_weak_subjects uws
            JOIN user_sub_discipline usd2
                ON uws.sub_discipline_id = usd2.sub_discipline_id
            JOIN sub_discipline sd
                ON usd2.sub_discipline_id = sd.id
            JOIN users u2
                ON usd2.user_id = u2.id
            WHERE usd2.user_id != :userId
        )
        SELECT
            my_id,
            matching_user_id,
            matching_user_username,
            MAX(CASE WHEN direction = 'user_help' THEN sub_discipline_name END) AS help_needed_subjects,
            MAX(CASE WHEN direction = 'matching_user_help' THEN sub_discipline_name END) AS help_provided_subjects,
            SUM(score) AS total_score
        FROM match_scores
        WHERE score > 0
        GROUP BY my_id, matching_user_id, matching_user_username
        ORDER BY total_score DESC
    """, nativeQuery = true)
    List<Object[]> findMatchesWithSelectedWeakSubjects(@Param("userId") Long userId, @Param("weakSubjectNames") List<String> weakSubjectNames);
}
