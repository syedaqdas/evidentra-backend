package com.evidentra.dto.forensics;

import com.evidentra.domain.enums.ForensicTaskStatus;
import com.evidentra.domain.enums.Priority;

import java.time.Instant;
import java.util.UUID;

public record ForensicTaskResponse(
        UUID id,
        UUID evidenceId,
        String evidenceNumber,
        String assignedToUsername,
        String taskName,
        String notes,
        ForensicTaskStatus status,
        Priority priority,
        Instant dueAt,
        Instant completedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
