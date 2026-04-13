package com.project.project_management.repository;

import com.project.project_management.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByClasses_ClassId(Long classId);
    List<AuditLog> findByClasses_ClassIdOrderByTimestampDesc(Long classId);
}
