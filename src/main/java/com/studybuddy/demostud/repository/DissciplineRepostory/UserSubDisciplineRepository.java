package com.studybuddy.demostud.repository.DissciplineRepostory;

import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.models.disciplines_package.UserSubDiscipline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSubDisciplineRepository extends JpaRepository<UserSubDiscipline,Long> {
    List<UserSubDiscipline> findByUserId(Long userId);

    Optional<UserSubDiscipline> findByUserIdAndSubDisciplineId(Long userId, Long subDisciplineId);

    List<UserSubDiscipline> findAllByUserIdAndSubDisciplineId(Long userId, Long subDisciplineId);
}
