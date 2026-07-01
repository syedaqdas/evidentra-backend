package com.evidentra.controller;

import com.evidentra.dto.casefile.CaseCreateRequest;
import com.evidentra.dto.casefile.CaseResponse;
import com.evidentra.service.CaseService;
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
@RequestMapping("/api/v1/cases")
public class CaseController {

    private final CaseService caseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public CaseResponse createCase(@Valid @RequestBody CaseCreateRequest request,
                                   Authentication authentication,
                                   HttpServletRequest servletRequest) {
        return caseService.createCase(request, authentication.getName(), servletRequest.getRemoteAddr());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<CaseResponse> listCases() {
        return caseService.listCases();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public CaseResponse getCase(@PathVariable UUID id) {
        return caseService.getCase(id);
    }
}
