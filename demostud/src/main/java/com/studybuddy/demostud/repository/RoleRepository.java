package com.studybuddy.demostud.repository;

import com.studybuddy.demostud.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);
}