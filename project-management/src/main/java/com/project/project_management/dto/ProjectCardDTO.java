package com.project.project_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProjectCardDTO {

    private String title;
    private List<String> members;
}

//used in class dashboard of teacher
//when teacher enters the class this page is seen
//for that we use this dto