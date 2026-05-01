package com.project.project_management.dto;

import lombok.Data;

import java.util.List;

@Data
public class StudentProjectDTO {

    private String projectTitle;
    private List<String> teammates;

    public StudentProjectDTO(String projectTitle, List<String> teammates) {
        this.projectTitle = projectTitle;
        this.teammates = teammates;
    }

    // getters
}