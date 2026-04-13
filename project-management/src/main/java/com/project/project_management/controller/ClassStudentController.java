package com.project.project_management.controller;

import com.project.project_management.service.ClassStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.project.project_management.entity.User;
import java.util.List;

@RestController
@RequestMapping("/student")
public class ClassStudentController {

    @Autowired
    private ClassStudentService classStudentService;

    @PostMapping("/join-class")
    public String joinClass(@RequestParam String classCode,
                            @RequestParam Long studentId) {

        return classStudentService.joinClass(classCode, studentId);
    }

    @DeleteMapping("/leave-class")
    public String leaveClass(@RequestParam String classCode,
                             @RequestParam Long studentId) {

        return classStudentService.leaveClass(classCode, studentId);
    }

    // 🔥 NEW API: GET all students in a class
    @GetMapping("/class/{classId}")
    public List<User> getStudentsByClass(@PathVariable Long classId) {
        return classStudentService.getStudentsByClass(classId);
    }
}