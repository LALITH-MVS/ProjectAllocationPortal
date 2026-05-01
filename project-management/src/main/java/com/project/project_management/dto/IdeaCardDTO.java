package com.project.project_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class IdeaCardDTO {

    private Long ideaId;
    private String title;
    private String description;
    private List<String> members;
}

//used in class dashboard of teacher
//when teacher enters the class this page is seen
//for that we use this dto