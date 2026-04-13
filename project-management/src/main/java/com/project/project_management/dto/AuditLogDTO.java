package com.project.project_management.dto;

import java.time.LocalDateTime;

public class AuditLogDTO {

    private String action;
    private String description;
    private LocalDateTime timestamp;

    public AuditLogDTO(String action, String description, LocalDateTime timestamp) {
        this.action = action;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getAction() { return action; }
    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }
}