package com.project.project_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SelectedProjectDTO {
    private String title;
    private List<String> teammates;
}
