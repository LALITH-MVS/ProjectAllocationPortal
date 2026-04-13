package com.project.project_management.repository;

import com.project.project_management.entity.Idea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IdeaRepository extends JpaRepository<Idea, Long> {

    List<Idea> findByClasses_ClassId(Long classId);
}