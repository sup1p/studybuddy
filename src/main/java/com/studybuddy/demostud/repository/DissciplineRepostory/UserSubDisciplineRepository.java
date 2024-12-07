package com.studybuddy.demostud.repository.DissciplineRepostory;

import com.studybuddy.demostud.models.disciplines_package.UserSubDiscipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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
            u2.avatar_path AS student_2_avatar_path, -- добавляем путь к аватару
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
                WHEN usd1.skill_level BETWEEN 8 AND 10 AND usd2.skill_level BETWEEN 6 AND 7 THEN 5
                ELSE 0
            END AS score,
            CASE
                WHEN usd1.skill_level < usd2.skill_level THEN 'student_1_help'
                ELSE 'student_2_help'
            END AS direction
        FROM user_sub_discipline usd1
        JOIN user_sub_discipline usd2
            ON usd1.sub_discipline_id = usd2.sub_discipline_id
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
        student_2_avatar_path, -- добавляем путь к аватару в результирующий набор
        MAX(CASE WHEN direction = 'student_1_help' THEN sub_discipline_name END) AS student_1_help_subjects,
        MAX(CASE WHEN direction = 'student_2_help' THEN sub_discipline_name END) AS student_2_help_subjects,
        SUM(score) AS total_score
    FROM match_scores
    WHERE score > 0
    GROUP BY student_1_id, student_2_id, student_2_username, student_2_avatar_path
    ORDER BY total_score DESC
    """, nativeQuery = true)
    List<Object[]> findMatchingPairsWithScores();

    @Modifying
    @Transactional
    @Query("DELETE FROM UserSubDiscipline usd WHERE usd.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
