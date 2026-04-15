package com.project.project_management.repository;

import com.project.project_management.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByClassId(Long classId);
    List<AuditLog> findByClassIdOrderByTimestampDesc(Long classId);
}
