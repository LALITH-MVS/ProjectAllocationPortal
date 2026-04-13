package com.project.project_management.repository;

import com.project.project_management.entity.Classes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassesRepository extends JpaRepository<Classes, Long> {

    Optional<Classes> findByClassCode(String classCode);
}

