package com.evidentra.dto.evidence;

import com.evidentra.domain.enums.ChainOfCustodyAction;

import java.time.Instant;
import java.util.UUID;

public record CustodyEntryResponse(
        UUID id,
        UUID evidenceId,
        ChainOfCustodyAction action,
        String fromCustodian,
        String toCustodian,
        String location,
        String notes,
        Instant occurredAt,
        String recordedByUsername
) {
}
