package com.evidentra.controller;

import com.evidentra.dto.audit.AuditLogResponse;
import com.evidentra.service.AuditService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/audit-logs")
public class AuditLogController {

    private final AuditService auditService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','AUDITOR')")
    public List<AuditLogResponse> listRecentAuditLogs(
            @RequestParam(defaultValue = "100") @Min(1) @Max(500) int limit) {
        return auditService.listRecent(limit);
    }
}
