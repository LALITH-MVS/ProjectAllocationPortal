package com.project.project_management.repository;

import com.project.project_management.entity.Classes;
import com.project.project_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassesRepository extends JpaRepository<Classes, Long> {

    Optional<Classes> findByClassCode(String classCode);
    List<Classes> findByTeacher(User teacher);
}

