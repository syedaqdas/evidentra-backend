package com.evidentra.dto.casefile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CaseCreateRequest(
        @NotBlank
        @Size(max = 60)
        String caseNumber,

        @NotBlank
        @Size(max = 200)
        String title,

        @Size(max = 4000)
        String description,

        @Size(max = 80)
        String leadOfficerUsername
) {
}
