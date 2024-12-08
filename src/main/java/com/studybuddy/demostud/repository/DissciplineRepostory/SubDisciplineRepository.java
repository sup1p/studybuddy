package com.studybuddy.demostud.repository.DissciplineRepostory;

import com.studybuddy.demostud.models.disciplines_package.SubDiscipline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SubDisciplineRepository extends JpaRepository<SubDiscipline, Long> {
    List<SubDiscipline> findByCategoryId(Long categoryId);

    List<SubDiscipline> findByNameIn(Collection<String> names);
}
