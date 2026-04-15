package com.project.project_management.service;
import com.project.project_management.entity.ClassStudent;
import com.project.project_management.entity.Classes;
import com.project.project_management.entity.User;
import com.project.project_management.repository.ClassStudentRepository;
import com.project.project_management.repository.ClassesRepository;
import com.project.project_management.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassStudentService {

    @Autowired
    private ClassesRepository classesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClassStudentRepository classStudentRepository;

    @Autowired
    private AuditLogService auditLogService;



    public String joinClass(String classCode) {

        // 🔐 Get logged-in user
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ❗ Role check
        if (!student.getRole().equals("STUDENT")) {
            throw new RuntimeException("Only students can join class");
        }

        Classes classes = classesRepository.findByClassCode(classCode)
                .orElseThrow(() -> new RuntimeException("Invalid class code"));

        // ❗ Prevent duplicate
        if (classStudentRepository.existsByClassesAndStudent(classes, student)) {
            return "Already joined";
        }

        ClassStudent cs = new ClassStudent();
        cs.setClasses(classes);
        cs.setStudent(student);

        classStudentRepository.save(cs);

        // 🔥 AUDIT LOG
        auditLogService.log(
                "CLASS_JOINED",
                student.getName() + " joined class " + classes.getSubjectName(),
                classes.getClassId()
        );

        return "Joined class successfully";
    }

    @Transactional
    public String leaveClass(String classCode) {

        // 🔐 Get logged-in user
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ❗ Role check
        if (!student.getRole().equals("STUDENT")) {
            throw new RuntimeException("Only students can leave class");
        }

        Classes classes = classesRepository.findByClassCode(classCode)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        // ❗ Check membership
        if (!classStudentRepository.existsByClassesAndStudent(classes, student)) {
            return "Student is not part of this class";
        }

        // 🔥 AUDIT LOG
        auditLogService.log(
                "CLASS_LEFT",
                student.getName() + " left class " + classes.getSubjectName(),
                classes.getClassId()
        );

        classStudentRepository.deleteByClassesAndStudent(classes, student);

        return "Left class successfully";
    }

    public List<User> getStudentsByClass(Long classId) {

        List<ClassStudent> classStudents =
                classStudentRepository.findByClasses_ClassId(classId);

        return classStudents.stream()
                .map(ClassStudent::getStudent)
                .toList();
    }
}