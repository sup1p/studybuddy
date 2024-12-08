package com.studybuddy.demostud.Service;

import com.studybuddy.demostud.DTOs.MatchingResultDefault;
import com.studybuddy.demostud.DTOs.SubDisciplineWithSkillLevelDto;
import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.models.disciplines_package.MatchingUser;
import com.studybuddy.demostud.models.disciplines_package.SubDiscipline;
import com.studybuddy.demostud.models.disciplines_package.UserSubDiscipline;
import com.studybuddy.demostud.repository.DissciplineRepostory.SubDisciplineRepository;
import com.studybuddy.demostud.repository.DissciplineRepostory.UserSubDisciplineRepository;
import com.studybuddy.demostud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchingService {

    private static UserSubDisciplineRepository userSubDisciplineRepository;
    @Autowired
    private SubDisciplineRepository subDisciplineRepository;

    @Autowired
    public void setUserSubDisciplineRepository(UserSubDisciplineRepository repository) {
        userSubDisciplineRepository = repository;
    }

    @Autowired
    private UserRepository userRepository;

    public static List<SubDisciplineWithSkillLevelDto> findSubjectsByUserId(Long userId) {
        return userSubDisciplineRepository.findByUserId(userId)
                .stream()
                .map(userSubDiscipline -> new SubDisciplineWithSkillLevelDto(
                        userSubDiscipline.getSubDiscipline(),
                        userSubDiscipline.getSkillLevel()
                ))
                .toList();
    }

    public List<MatchingResultDefault> getDefaultRecommendations(Long userId) {
        List<Object[]> results = userSubDisciplineRepository.findMatchingPairsWithScores();

        // Transform result of SQL-request into DTO
        return results.stream()
                .map(row -> new MatchingResultDefault(
                        (Long) row[0], // my_id
                        (Long) row[1], // buddies_id
                        (String) row[2], // buddies_username
                        (String) row[3], // buddie avatar path
                        (String) row[4], //buddie help_needed_subjects
                        (String) row[5], //buddie help_provided_subjects
                        (Long) row[6]  // total_score
                ))
                .collect(Collectors.toList());
    }

    public List<MatchingUser> findMatchingUsers(User currentUser, List<String> weakSubjects, String genderFilter, boolean locationFilter) {
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

        // Преобразуем в список MatchingUser
        return userScores.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue() - e1.getValue()) // Сортировка по очкам
                .map(entry -> new MatchingUser(entry.getKey(), entry.getValue())) // Преобразуем в MatchingUser
                .toList();
    }

    private List<SubDiscipline> mapDisciplinesToEntities(List<String> disciplineNames) {
        // Получаем список субдисциплин по названиям
        List<SubDiscipline> disciplines = subDisciplineRepository.findByNameIn(disciplineNames);

        // Проверяем, найдены ли все дисциплины
        List<String> missingNames = disciplineNames.stream()
                .filter(name -> disciplines.stream().noneMatch(d -> d.getName().equalsIgnoreCase(name)))
                .toList();

        if (!missingNames.isEmpty()) {
            System.err.println("Следующие субдисциплины не найдены: " + missingNames);
        }

        // Убираем категории
        return disciplines.stream()
                .map(discipline -> {
                    SubDiscipline copy = new SubDiscipline();
                    copy.setId(discipline.getId());
                    copy.setName(discipline.getName());
                    copy.setCategory(null); // Убираем категорию
                    return copy;
                })
                .toList();
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

        // Получаем дисциплины текущего пользователя
        List<UserSubDiscipline> currentUserSubDisciplines = userSubDisciplineRepository.findAll().stream()
                .filter(ud -> ud.getUser().equals(currentUser) && disciplines.contains(ud.getSubDiscipline()))
                .toList();

        System.out.println("Current user disciplines: " + currentUserSubDisciplines);

        for (UserSubDiscipline userSub : users) {
            User user = userSub.getUser();
            int score = 0;

            // Получаем дисциплины другого пользователя
            List<UserSubDiscipline> userDisciplines = userSubDisciplineRepository.findAll().stream()
                    .filter(ud -> ud.getUser().equals(user))
                    .toList();

            System.out.println("User: " + user.getUsername() + ", Disciplines: " + userDisciplines);

            for (UserSubDiscipline currentDiscipline : currentUserSubDisciplines) {
                for (UserSubDiscipline userSubDiscipline : userDisciplines) {
                    if (currentDiscipline.getSubDiscipline().equals(userSubDiscipline.getSubDiscipline())) {
                        System.out.println("Match found: " + currentDiscipline.getSubDiscipline().getName());

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

                        System.out.println("Score for this match: " + score);
                    }
                }
            }

            System.out.println("Total score for user " + user.getUsername() + ": " + score);
            userScores.put(user, score);
        }

        return userScores;
    }


}


