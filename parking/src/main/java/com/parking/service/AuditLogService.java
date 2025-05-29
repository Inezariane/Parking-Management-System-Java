package com.parking.service;

import com.parking.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface AuditLogService {
    void logAction(Long userId, String action, String description, String detailsJson);
    Page<AuditLog> listLogs(String action, Long userId, String search, Pageable pageable);
    AuditLog getLog(Long id);
} 