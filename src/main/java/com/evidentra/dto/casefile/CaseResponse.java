package com.evidentra.dto.casefile;

import com.evidentra.domain.enums.CaseStatus;

import java.time.Instant;
import java.util.UUID;

public record CaseResponse(
        UUID id,
        String caseNumber,
        String title,
        String description,
        CaseStatus status,
        String leadOfficerUsername,
        Instant openedAt,
        Instant closedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
