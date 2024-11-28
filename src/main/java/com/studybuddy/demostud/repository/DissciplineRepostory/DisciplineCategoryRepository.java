package com.studybuddy.demostud.repository.DissciplineRepostory;


import com.studybuddy.demostud.models.disciplines_package.DisciplineCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  DisciplineCategoryRepository extends JpaRepository<DisciplineCategory, Long> {
}
