package com.studybuddy.demostud.repository;

import com.studybuddy.demostud.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Set<Role> findByRoleName(String roleName);
}
