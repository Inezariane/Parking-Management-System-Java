package com.parking.service.impl;

import com.parking.entity.AuditLog;
import com.parking.repository.AuditLogRepository;
import com.parking.service.AuditLogService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {
    private final AuditLogRepository auditLogRepository;

    @Override
    public void logAction(Long userId, String action, String description, String details) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setDescription(description);
        log.setDetails(details);
        auditLogRepository.save(log);
    }

    @Override
    public Page<AuditLog> listLogs(String action, Long userId, String search, Pageable pageable) {
        Specification<AuditLog> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (action != null && !action.isEmpty()) {
                predicates.add(cb.equal(root.get("action"), action));
            }
            
            if (userId != null) {
                predicates.add(cb.equal(root.get("userId"), userId));
            }
            
            if (search != null && !search.isEmpty()) {
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("description")), "%" + search.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("details")), "%" + search.toLowerCase() + "%")
                ));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return auditLogRepository.findAll(spec, pageable);
    }

    @Override
    public AuditLog getLog(Long id) {
        return auditLogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Audit log not found with id: " + id));
    }
} 