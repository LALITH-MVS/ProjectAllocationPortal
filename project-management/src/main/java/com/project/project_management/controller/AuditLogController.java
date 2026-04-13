package com.project.project_management.controller;

import com.project.project_management.dto.AuditLogDTO;
import com.project.project_management.service.AuditLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit")
public class AuditLogController {

    private final AuditLogService auditLogService;

    // ✅ Constructor Injection (clean)
    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    // 🔥 GET logs for a specific class (DTO version)
    @GetMapping("/class/{classId}")
    public List<AuditLogDTO> getLogsByClass(@PathVariable Long classId) {
        return auditLogService.getLogsByClass(classId);
    }
}