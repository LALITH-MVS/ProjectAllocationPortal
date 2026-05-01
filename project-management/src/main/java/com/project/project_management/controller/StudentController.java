package com.project.project_management.controller;

import com.project.project_management.dto.StudentClassDTO;
import com.project.project_management.dto.StudentIdeaDTO;
import com.project.project_management.dto.StudentProfileDTO;
import com.project.project_management.dto.StudentProjectDTO;
import com.project.project_management.service.StudentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/profile")
    public StudentProfileDTO getProfile() {
        return studentService.getProfile();
    }

    @GetMapping("/classes")
    public List<StudentClassDTO> getClasses() {
        return studentService.getMyClasses();
    }

    @GetMapping("/projects")
    public List<StudentProjectDTO> getProjects() {
        return studentService.getMyProjects();
    }

    @GetMapping("/ideas")
    public List<StudentIdeaDTO> getIdeas() {
        return studentService.getMyIdeas();
    }
}
