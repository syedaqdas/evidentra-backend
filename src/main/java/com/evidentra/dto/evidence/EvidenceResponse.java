package com.evidentra.dto.evidence;

import com.evidentra.domain.enums.EvidenceStatus;
import com.evidentra.domain.enums.EvidenceType;

import java.time.Instant;
import java.util.UUID;

public record EvidenceResponse(
        UUID id,
        String evidenceNumber,
        UUID caseId,
        String caseNumber,
        EvidenceType type,
        EvidenceStatus status,
        String description,
        String storageLocation,
        String fileName,
        String contentType,
        String sha256Hash,
        Long sizeBytes,
        Instant collectedAt,
        String collectedByUsername,
        Instant createdAt,
        Instant updatedAt
) {
}
