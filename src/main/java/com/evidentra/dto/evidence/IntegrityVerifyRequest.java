package com.evidentra.dto.evidence;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record IntegrityVerifyRequest(
        @NotBlank
        @Pattern(regexp = "^[A-Fa-f0-9]{64}$", message = "must be a SHA-256 hex digest")
        String sha256Hash
) {
}
