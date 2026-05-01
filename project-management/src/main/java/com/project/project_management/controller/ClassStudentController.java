package com.project.project_management.controller;

import com.project.project_management.entity.User;
import com.project.project_management.service.ClassStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
public class ClassStudentController {

    @Autowired
    private ClassStudentService classStudentService;

    // ✅ FIXED: removed studentId
    @PostMapping("/join-class")
    public String joinClass(@RequestParam String classCode) {
        return classStudentService.joinClass(classCode);
    }

    // ✅ FIXED: removed studentId
    @DeleteMapping("/leave-class")
    public String leaveClass(@RequestParam String classCode) {
        return classStudentService.leaveClass(classCode);
    }

    // ✅ unchanged
    @GetMapping("/class/{classId}")
    public List<User> getStudentsByClass(@PathVariable Long classId) {
        return classStudentService.getStudentsByClass(classId);
    }
}

