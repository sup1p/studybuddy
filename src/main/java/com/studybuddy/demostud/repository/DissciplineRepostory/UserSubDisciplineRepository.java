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
    WITH user_weak_subjects AS (
        -- Определение слабых сторон пользователя
        SELECT :userId AS user_id, sd.id AS sub_discipline_id
        FROM sub_discipline sd
        WHERE sd.name IN :weakSubjects -- Пользователь вводит слабые предметы в запросе, чтобы найти тех, кто силен в этих дисциплинах
    ),
    match_scores AS (
        -- Определение возможных пар для взаимопомощи
        SELECT
            usd1.user_id AS student_1_id, -- ID первого студента (потенциальный помощник)
            usd2.user_id AS student_2_id, -- ID второго студента (текущий пользователь)
            sd.name AS sub_discipline_name, -- Название дисциплины, по которой осуществляется сравнение
            CASE
                -- Раздача очков в зависимости от уровня навыков первого и второго студента
                WHEN usd1.skill_level BETWEEN 6 AND 10 AND usd2.skill_level BETWEEN 1 AND 5 THEN 15
                ELSE 0 -- Если уровни не соответствуют условиям выше, очки равны нулю
            END AS score,
            'student_1_help' AS direction -- Всегда student_1 помогает student_2
        FROM user_sub_discipline usd1
        JOIN user_sub_discipline usd2
            ON usd1.sub_discipline_id = usd2.sub_discipline_id -- Совпадение по одной и той же дисциплине
        JOIN sub_discipline sd
            ON usd1.sub_discipline_id = sd.id
        WHERE 
            usd2.sub_discipline_id IN (SELECT sub_discipline_id FROM user_weak_subjects) -- Ищем тех, кто силен в слабых сторонах пользователя
            AND usd1.skill_level BETWEEN 6 AND 10 -- Фильтруем только тех, у кого высокий уровень навыков
    )
    -- Итоговый запрос для группировки и вывода результатов
    SELECT
        student_1_id, -- ID потенциального помощника
        student_2_id, -- ID текущего пользователя
        -- Определение дисциплины, в которой первый студент помогает
        MAX(sub_discipline_name) AS student_1_help_subjects,
        SUM(score) AS total_score -- Общий балл за помощь, для определения полезности взаимодействия
    FROM match_scores
    WHERE score > 0 -- Оставляем только те пары, где есть какая-то полезность
    GROUP BY student_1_id, student_2_id -- Группируем по парам студентов
    ORDER BY total_score DESC -- Сортируем по общему баллу в порядке убывания
""", nativeQuery = true)
// Ищет студентов, которые сильны в дисциплинах, указанных как слабые текущим пользователем

    List<Object[]> findMatchesWithSelectedWeakSubjects(@Param("userId") Long userId, @Param("weakSubjects") List<String> weakSubjects);

    @Query(value = """
        WITH match_scores AS (
            SELECT
                usd1.user_id AS student_1_id,
                usd2.user_id AS student_2_id,
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
        )
        SELECT
            student_1_id,
            student_2_id,
            MAX(CASE WHEN direction = 'student_1_help' THEN sub_discipline_name END) AS student_1_help_subjects,
            MAX(CASE WHEN direction = 'student_2_help' THEN sub_discipline_name END) AS student_2_help_subjects,
            SUM(score) AS total_score --вычисляется общая сумма уровня взаимопомощи
        FROM match_scores
        WHERE score > 0
        GROUP BY student_1_id, student_2_id
        ORDER BY total_score DESC
    """, nativeQuery = true)
    //рекомендации, лишь возвращает студентов на основе заранее определенных на аккаунте слабых дисциплин
    List<Object[]> findMatchingPairsWithScores();
}
