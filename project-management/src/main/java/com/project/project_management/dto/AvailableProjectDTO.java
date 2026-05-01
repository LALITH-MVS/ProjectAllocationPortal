package com.project.project_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AvailableProjectDTO {
    private Long projectId;
    private String title;
    private String description;
}
