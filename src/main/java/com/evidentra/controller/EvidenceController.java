package com.evidentra.controller;

import com.evidentra.dto.evidence.CustodyEntryResponse;
import com.evidentra.dto.evidence.CustodyTransferRequest;
import com.evidentra.dto.evidence.EvidenceCreateRequest;
import com.evidentra.dto.evidence.EvidenceResponse;
import com.evidentra.dto.evidence.IntegrityVerifyRequest;
import com.evidentra.dto.evidence.IntegrityVerifyResponse;
import com.evidentra.service.EvidenceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/evidence")
public class EvidenceController {

    private final EvidenceService evidenceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER','EVIDENCE_CUSTODIAN')")
    public EvidenceResponse createEvidence(@Valid @RequestBody EvidenceCreateRequest request,
                                           Authentication authentication,
                                           HttpServletRequest servletRequest) {
        return evidenceService.createEvidence(request, authentication.getName(), servletRequest.getRemoteAddr());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public EvidenceResponse getEvidence(@PathVariable UUID id) {
        return evidenceService.getEvidence(id);
    }

    @GetMapping("/case/{caseId}")
    @PreAuthorize("isAuthenticated()")
    public List<EvidenceResponse> listEvidenceByCase(@PathVariable UUID caseId) {
        return evidenceService.listEvidenceByCase(caseId);
    }

    @PostMapping("/{id}/verify")
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER','FORENSIC_ANALYST','EVIDENCE_CUSTODIAN')")
    public IntegrityVerifyResponse verifyIntegrity(@PathVariable UUID id,
                                                   @Valid @RequestBody IntegrityVerifyRequest request,
                                                   Authentication authentication,
                                                   HttpServletRequest servletRequest) {
        return evidenceService.verifyIntegrity(id, request.sha256Hash(), authentication.getName(),
                servletRequest.getRemoteAddr());
    }

    @PostMapping("/{id}/custody")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER','EVIDENCE_CUSTODIAN')")
    public CustodyEntryResponse transferCustody(@PathVariable UUID id,
                                                @Valid @RequestBody CustodyTransferRequest request,
                                                Authentication authentication,
                                                HttpServletRequest servletRequest) {
        return evidenceService.transferCustody(id, request, authentication.getName(), servletRequest.getRemoteAddr());
    }

    @GetMapping("/{id}/custody")
    @PreAuthorize("isAuthenticated()")
    public List<CustodyEntryResponse> listCustody(@PathVariable UUID id) {
        return evidenceService.listCustody(id);
    }
}
