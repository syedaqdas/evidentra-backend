package com.evidentra.dto.evidence;

import com.evidentra.domain.enums.EvidenceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record EvidenceCreateRequest(
        @NotNull UUID caseId,

        @NotBlank
        @Size(max = 80)
        String evidenceNumber,

        @NotNull EvidenceType type,

        @Size(max = 4000)
        String description,

        @Size(max = 180)
        String storageLocation,

        @Size(max = 255)
        String fileName,

        @Size(max = 120)
        String contentType,

        @Pattern(regexp = "^[A-Fa-f0-9]{64}$", message = "must be a SHA-256 hex digest")
        String sha256Hash,

        @PositiveOrZero
        Long sizeBytes
) {
}
