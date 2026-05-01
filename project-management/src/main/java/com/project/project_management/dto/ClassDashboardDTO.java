package com.project.project_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ClassDashboardDTO {

    private int totalStudents;
    private int totalProjects;
    private int totalTeams;
    private int pendingIdeas;

    // ✅ NEW
    private List<ProjectCardDTO> selectedProjects;
    private List<ProjectCardDTO> availableProjects;

    private List<IdeaCardDTO> ideas;
}

//used in class dashboard of teacher
//when teacher enters the class this page is seen
//for that we use this dto