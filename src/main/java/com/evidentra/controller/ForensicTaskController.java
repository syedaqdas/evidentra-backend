package com.evidentra.controller;

import com.evidentra.dto.forensics.CompleteTaskRequest;
import com.evidentra.dto.forensics.ForensicTaskRequest;
import com.evidentra.dto.forensics.ForensicTaskResponse;
import com.evidentra.service.ForensicTaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
@RequestMapping("/api/v1/forensics/tasks")
public class ForensicTaskController {

    private final ForensicTaskService forensicTaskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','FORENSIC_ANALYST')")
    public ForensicTaskResponse createTask(@Valid @RequestBody ForensicTaskRequest request,
                                           Authentication authentication,
                                           HttpServletRequest servletRequest) {
        return forensicTaskService.createTask(request, authentication.getName(), servletRequest.getRemoteAddr());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FORENSIC_ANALYST','OFFICER')")
    public List<ForensicTaskResponse> listTasks() {
        return forensicTaskService.listTasks();
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN','FORENSIC_ANALYST')")
    public ForensicTaskResponse completeTask(@PathVariable UUID id,
                                             @Valid @RequestBody CompleteTaskRequest request,
                                             Authentication authentication,
                                             HttpServletRequest servletRequest) {
        return forensicTaskService.completeTask(id, request, authentication.getName(), servletRequest.getRemoteAddr());
    }
}
