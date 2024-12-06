package com.studybuddy.demostud.Service;

import com.studybuddy.demostud.DTOs.MatchingResultDefault;
import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.models.disciplines_package.SubDiscipline;
import com.studybuddy.demostud.models.disciplines_package.UserSubDiscipline;
import com.studybuddy.demostud.repository.DissciplineRepostory.UserSubDisciplineRepository;
import com.studybuddy.demostud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MatchingService {

    @Autowired
    private static UserSubDisciplineRepository userSubDisciplineRepository;

    @Autowired
    private UserRepository userRepository;

    public static List<SubDiscipline> findSubjectsByUserId(Long userId) {
        return userSubDisciplineRepository.findByUserId(userId)
                .stream()
                .map(UserSubDiscipline::getSubDiscipline)
                .toList();
    }

    public List<MatchingResultDefault> getDefaultRecommendations(Long userId) {
        List<Object[]> results = userSubDisciplineRepository.findMatchingPairsWithScores();

        // Transform result of SQL-request into DTO
        return results.stream()
                .map(row -> new MatchingResultDefault(
                        (Long) row[0], // matching_user_id
                        (Long) row[1], // buddies_id
                        (String) row[2], // buddies_username
                        (String) row[3], // help_provided_subjects
                        (String) row[4], // help_needed_subjects
                        (Long) row[5]  // total_score
                ))
                .collect(Collectors.toList());
    }

    public List<User> findMatchingUsers(User currentUser, List<String> weakSubjects, String genderFilter, boolean locationFilter) {
        // Преобразуем названия дисциплин в сущности
        List<SubDiscipline> weakDisciplines = mapDisciplinesToEntities(weakSubjects);

        // Получаем всех пользователей
        List<UserSubDiscipline> allUsers = userSubDisciplineRepository.findAll();

        // Применяем фильтр по полу (genderFilter)
        if (genderFilter != null) {
            allUsers = allUsers.stream()
                    .filter(userSub -> userSub.getUser().getGender().equalsIgnoreCase(genderFilter))
                    .toList();
        }

        // Применяем фильтр по местоположению (locationFilter)
        if (locationFilter) {
            allUsers = allUsers.stream()
                    .filter(userSub -> userSub.getUser().getCountry().equalsIgnoreCase(currentUser.getCountry()))
                    .toList();
        }

        // Фильтруем пользователей, у которых есть хотя бы одна из дисциплин
        List<UserSubDiscipline> potentialUsers = filterUsersByDisciplines(allUsers, weakDisciplines);

        // Фильтруем пользователей с уровнем знаний 6+
        List<UserSubDiscipline> filteredUsers = filterUsersByMinLevel(potentialUsers, weakDisciplines);

        // Рассчитываем очки для каждого пользователя
        Map<User, Integer> userScores = calculateMatchingScores(currentUser, weakDisciplines, filteredUsers);

        // Сортируем по очкам
        return userScores.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue() - e1.getValue())
                .map(Map.Entry::getKey)
                .toList();
    }

    private List<SubDiscipline> mapDisciplinesToEntities(List<String> disciplineNames) {
        // Здесь должна быть логика преобразования названий в сущности Discipline
        return new ArrayList<>();
    }

    private List<UserSubDiscipline> filterUsersByDisciplines(
            List<UserSubDiscipline> users,
            List<SubDiscipline> subDisciplines) {

        return users.stream()
                .filter(user -> subDisciplines.contains(user.getSubDiscipline()))
                .toList();
    }

    private List<UserSubDiscipline> filterUsersByMinLevel(
            List<UserSubDiscipline> users,
            List<SubDiscipline> subDisciplines) {

        return users.stream()
                .filter(user -> user.getSkillLevel() >= 6)
                .toList();
    }

    private Map<User, Integer> calculateMatchingScores(User currentUser, List<SubDiscipline> disciplines, List<UserSubDiscipline> users) {
        Map<User, Integer> userScores = new HashMap<>();

        List<UserSubDiscipline> currentUserSubDisciplines = userSubDisciplineRepository.findAll().stream()
                .filter(ud -> ud.getUser().equals(currentUser) && disciplines.contains(ud.getSubDiscipline()))
                .toList();

        for (UserSubDiscipline user : users) {
            int score = 0;
            List<UserSubDiscipline> userDisciplines = userSubDisciplineRepository.findAll().stream()
                    .filter(ud -> ud.getUser().equals(user))
                    .toList();

            for (UserSubDiscipline currentDiscipline : currentUserSubDisciplines) {
                for (UserSubDiscipline userSubDiscipline : userDisciplines) {
                    if (currentDiscipline.getSubDiscipline().equals(userSubDiscipline.getSubDiscipline())) {
                        // Сильные знания
                        if (userSubDiscipline.getSkillLevel() >= 6 && userSubDiscipline.getSkillLevel() <= 7) {
                            score += 10;
                        } else if (userSubDiscipline.getSkillLevel() >= 8) {
                            score += 15;
                        }
                        // Взаимопомощь
                        if (currentDiscipline.getSkillLevel() >= 8 && userSubDiscipline.getSkillLevel() <= 3) {
                            score += 7;
                        } else if (currentDiscipline.getSkillLevel() >= 6 && userSubDiscipline.getSkillLevel() <= 5) {
                            score += 3;
                        }
                    }
                }
            }
            userScores.put(user.getUser(), score);
        }
        return userScores;
    }
}


