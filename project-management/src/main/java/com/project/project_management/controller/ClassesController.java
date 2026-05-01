package com.project.project_management.controller;

import com.project.project_management.dto.ClassDashboardDTO;
import com.project.project_management.dto.TeacherClassDTO;
import com.project.project_management.entity.Classes;
import com.project.project_management.service.ClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class ClassesController {

    @Autowired
    private ClassesService classesService;

    @PostMapping("/create")
    public Classes createClass(@RequestBody Classes classes) {
        return classesService.createClass(classes);
    }

    @DeleteMapping("/delete")
    public String deleteClass(@RequestParam Long classId,
                              @RequestParam Long teacherId) {

        classesService.deleteClass(classId, teacherId);
        return "Class deleted successfully";
    }

    // 🔥🔥 ADD THIS (VERY IMPORTANT)
    @GetMapping("/my-classes")
    public List<TeacherClassDTO> getMyClasses() {
        return classesService.getMyClasses();
    }

    // 🔥 CLASS DASHBOARD API
    @GetMapping("/dashboard/{classId}")
    public ClassDashboardDTO getClassDashboard(@PathVariable Long classId) {
        return classesService.getClassDashboard(classId);
    }
}

