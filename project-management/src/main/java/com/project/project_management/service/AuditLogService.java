package com.project.project_management.service;

import com.project.project_management.dto.AuditLogDTO;
import com.project.project_management.entity.AuditLog;
import com.project.project_management.entity.Classes;
import com.project.project_management.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // 🔥 CREATE LOG
    public void log(String action, String description, Long classId) {

        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setDescription(description);
        log.setTimestamp(LocalDateTime.now());
        log.setClassId(classId);   // ✅ FIXED

        auditLogRepository.save(log);
    }

    // 🔥 GET CLASS-WISE LOGS (CLEAN RESPONSE)
    public List<AuditLogDTO> getLogsByClass(Long classId) {

        List<AuditLog> logs =
                auditLogRepository.findByClassIdOrderByTimestampDesc(classId);
        return logs.stream()
                .map(log -> new AuditLogDTO(
                        log.getAction(),
                        log.getDescription(),
                        log.getTimestamp()
                ))
                .toList();
    }
}