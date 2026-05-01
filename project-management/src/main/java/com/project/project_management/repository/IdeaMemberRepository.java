package com.project.project_management.repository;

import com.project.project_management.entity.Idea;
import com.project.project_management.entity.IdeaMember;
import com.project.project_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IdeaMemberRepository extends JpaRepository<IdeaMember, Long> {

    // 🔥 ADD THIS
    List<IdeaMember> findByIdea(Idea idea);

    List<IdeaMember> findByStudent(User student);
}