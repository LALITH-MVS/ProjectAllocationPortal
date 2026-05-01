package com.project.project_management.repository;

import com.project.project_management.entity.ClassStudent;
import com.project.project_management.entity.Classes;
import com.project.project_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassStudentRepository extends JpaRepository<ClassStudent, Long> {

    boolean existsByClassesAndStudent(Classes classes, User student);

    void deleteByClassesAndStudent(Classes classes, User student);

    List<ClassStudent> findByClasses_ClassId(Long classId);

    // 🔥 ADD THIS
    List<ClassStudent> findByStudent(User student);
}