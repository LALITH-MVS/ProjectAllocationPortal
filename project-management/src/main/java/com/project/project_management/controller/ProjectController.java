package com.project.project_management.controller;

import com.project.project_management.entity.Project;
import com.project.project_management.service.ProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // 🔹 Teacher adds project
    @PostMapping("/add")
    public String addProject(@RequestParam Long classId,
                             @RequestParam String title,
                             @RequestParam String description) {

        return projectService.addProject(classId, title, description);
    }

    // 🔹 Student views projects
    @GetMapping("/{classId}")
    public List<Project> getProjects(@PathVariable Long classId) {

        return projectService.getProjectsByClass(classId);
    }

    @DeleteMapping("/delete/{projectId}")
    public String deleteProject(@PathVariable Long projectId) {

        return projectService.deleteProject(projectId);
    }

    @PostMapping("/select")
    public String selectProject(@RequestParam Long projectId,
                                @RequestBody List<Long> studentIds) {

        return projectService.selectProject(projectId, studentIds);
    }

}