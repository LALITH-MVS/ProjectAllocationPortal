package com.project.project_management.dto;

public class StudentIdeaDTO {

    private String title;
    private String status;

    public StudentIdeaDTO(String title, String status) {
        this.title = title;
        this.status = status;
    }

    public String getTitle() { return title; }
    public String getStatus() { return status; }
}
