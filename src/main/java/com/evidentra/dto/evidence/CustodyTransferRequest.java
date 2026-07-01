package com.evidentra.dto.evidence;

import com.evidentra.domain.enums.ChainOfCustodyAction;
import com.evidentra.domain.enums.EvidenceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustodyTransferRequest(
        @Size(max = 160)
        String fromCustodian,

        @NotBlank
        @Size(max = 160)
        String toCustodian,

        @Size(max = 180)
        String location,

        @Size(max = 4000)
        String notes,

        ChainOfCustodyAction action,
        EvidenceStatus newStatus
) {
}
