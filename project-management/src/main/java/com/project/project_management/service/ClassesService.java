package com.project.project_management.service;

import com.project.project_management.entity.Classes;
import com.project.project_management.entity.User;
import com.project.project_management.repository.ClassesRepository;
import com.project.project_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClassesService {

    @Autowired
    private ClassesRepository classesRepository;

    @Autowired
    private UserRepository userRepository;   // 🔥 ADD THIS

    @Autowired
    private AuditLogService auditLogService;

    public Classes createClass(Classes classes) {

        // 🔥 check duplicate class code
        if (classesRepository.findByClassCode(classes.getClassCode()).isPresent()) {
            throw new RuntimeException("Class code already exists!");
        }

        // 🔥 FIX: Fetch full teacher from DB
        Long teacherId = classes.getTeacher().getUserId();

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        classes.setTeacher(teacher);   // ✅ IMPORTANT

        Classes savedClass = classesRepository.save(classes);

        // 🔥 AUDIT LOG
        auditLogService.log(
                "CLASS_CREATED",
                "Teacher created class " + savedClass.getSubjectName(),
                savedClass
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

        auditLogService.log(
                "CLASS_DELETED",
                "Teacher deleted class " + classes.getSubjectName(),
                classes
        );
        classesRepository.delete(classes);
    }
}

