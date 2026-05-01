package com.project.project_management.dto;

import lombok.Data;
import java.util.List;

@Data
public class IdeaSubmitDTO {

    private Long classId;
    private String title;
    private String description;
    private List<Long> studentIds;

}

