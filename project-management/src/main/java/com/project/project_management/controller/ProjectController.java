package com.project.project_management.controller;

import com.project.project_management.dto.AvailableProjectDTO;
import com.project.project_management.dto.ProjectSelectDTO;
import com.project.project_management.dto.SelectedProjectDTO;
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
    public String selectProject(@RequestBody ProjectSelectDTO dto) {
        return projectService.selectProject(dto.getProjectId(), dto.getStudentIds());
    }

    @DeleteMapping("/leave")
    public String leaveProject(@RequestParam Long teamId) {
        return projectService.leaveProject(teamId);
    }

    // 🔥 NEW: SELECTED PROJECTS (FOR UI)
    @GetMapping("/selected/{classId}")
    public List<SelectedProjectDTO> getSelectedProjects(@PathVariable Long classId) {
        return projectService.getSelectedProjects(classId);
    }

    // 🔥 NEW: AVAILABLE PROJECTS (FOR UI)
    @GetMapping("/available/{classId}")
    public List<AvailableProjectDTO> getAvailableProjects(@PathVariable Long classId) {
        return projectService.getAvailableProjects(classId);
    }

}