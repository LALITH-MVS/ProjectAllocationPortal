package com.project.project_management.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProjectSelectDTO {
    private Long projectId;
    private List<Long> studentIds;
}
