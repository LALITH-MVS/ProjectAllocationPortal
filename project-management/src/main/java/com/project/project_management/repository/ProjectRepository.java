package com.project.project_management.repository;

import com.project.project_management.entity.Project;
import com.project.project_management.entity.Classes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByClasses(Classes classes);
    List<Project> findByClasses_ClassIdAndStatus(Long classId, String status);
}