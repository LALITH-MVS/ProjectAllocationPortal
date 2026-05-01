package com.project.project_management.repository;

import com.project.project_management.entity.Project;
import com.project.project_management.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Team findByProject(Project project);
}