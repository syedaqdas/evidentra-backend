package com.evidentra.dto.audit;

import java.time.Instant;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        String actorUsername,
        String action,
        String resourceType,
        String resourceId,
        String summary,
        String sourceIp,
        Instant createdAt
) {
}
