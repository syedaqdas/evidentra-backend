package com.evidentra.service;

import com.evidentra.domain.entity.AuditLog;
import com.evidentra.dto.audit.AuditLogResponse;
import com.evidentra.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String actorUsername, String action, String resourceType, UUID resourceId,
                       String summary, String sourceIp) {
        auditLogRepository.save(AuditLog.builder()
                .actorUsername(actorUsername == null ? "system" : actorUsername)
                .action(action)
                .resourceType(resourceType)
                .resourceId(resourceId == null ? null : resourceId.toString())
                .summary(summary)
                .sourceIp(sourceIp)
                .build());
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> listRecent(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 500));
        return auditLogRepository
                .findAll(PageRequest.of(0, safeLimit, Sort.by(Sort.Direction.DESC, "createdAt")))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AuditLogResponse toResponse(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getActorUsername(),
                log.getAction(),
                log.getResourceType(),
                log.getResourceId(),
                log.getSummary(),
                log.getSourceIp(),
                log.getCreatedAt()
        );
    }
}
