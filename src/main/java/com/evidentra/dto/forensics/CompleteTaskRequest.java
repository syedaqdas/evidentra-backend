package com.evidentra.dto.forensics;

import jakarta.validation.constraints.Size;

public record CompleteTaskRequest(
        @Size(max = 4000)
        String notes
) {
}
