package com.evidentra.dto.forensics;

import com.evidentra.domain.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public record ForensicTaskRequest(
        @NotNull UUID evidenceId,

        @Size(max = 80)
        String assignedToUsername,

        @NotBlank
        @Size(max = 180)
        String taskName,

        @Size(max = 4000)
        String notes,

        Priority priority,
        Instant dueAt
) {
}
