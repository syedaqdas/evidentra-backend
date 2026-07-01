package com.evidentra.dto.evidence;

import java.time.Instant;
import java.util.UUID;

public record IntegrityVerifyResponse(
        UUID evidenceId,
        String evidenceNumber,
        String storedSha256Hash,
        String providedSha256Hash,
        boolean verified,
        Instant verifiedAt
) {
}
