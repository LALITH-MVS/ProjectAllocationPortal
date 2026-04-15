package com.project.project_management.service;

import com.project.project_management.entity.Classes;
import com.project.project_management.entity.User;
import com.project.project_management.repository.ClassesRepository;
import com.project.project_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ClassesService {

    private final ClassesRepository classesRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public ClassesService(ClassesRepository classesRepository,
                          UserRepository userRepository,
                          AuditLogService auditLogService) {
        this.classesRepository = classesRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }


    public Classes createClass(Classes classes) {

        // 🔥 Get logged-in user email from JWT
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // 🔥 Fetch user from DB
        User teacher = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔥 ROLE CHECK (extra safety)
        if (!teacher.getRole().equals("TEACHER")) {
            throw new RuntimeException("Only teachers can create class");
        }

        // 🔥 assign teacher automatically
        classes.setTeacher(teacher);

        // duplicate check
        if (classesRepository.findByClassCode(classes.getClassCode()).isPresent()) {
            throw new RuntimeException("Class code already exists!");
        }

        Classes savedClass = classesRepository.save(classes);

        // audit log
        auditLogService.log(
                "CLASS_CREATED",
                "Teacher created class " + savedClass.getSubjectName(),
                savedClass.getClassId()
        );

        return savedClass;
    }

    public void deleteClass(Long classId, Long teacherId) {

        Classes classes = classesRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        // 🔥 check ownership
        if (!classes.getTeacher().getUserId().equals(teacherId)) {
            throw new RuntimeException("You are not allowed to delete this class");
        }

        String subjectName = classes.getSubjectName();

        classesRepository.delete(classes);

        auditLogService.log(
                "CLASS_DELETED",
                "Teacher deleted class " + subjectName,
                classId
        );
    }
}

