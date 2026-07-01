package com.evidentra.service;

import com.evidentra.domain.entity.EvidenceItem;
import com.evidentra.domain.entity.ForensicTask;
import com.evidentra.domain.entity.UserEntity;
import com.evidentra.domain.enums.ForensicTaskStatus;
import com.evidentra.dto.forensics.CompleteTaskRequest;
import com.evidentra.dto.forensics.ForensicTaskRequest;
import com.evidentra.dto.forensics.ForensicTaskResponse;
import com.evidentra.exception.ResourceNotFoundException;
import com.evidentra.repository.ForensicTaskRepository;
import com.evidentra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForensicTaskService {

    private final ForensicTaskRepository forensicTaskRepository;
    private final EvidenceService evidenceService;
    private final UserRepository userRepository;
    private final AuditService auditService;

    @Transactional
    public ForensicTaskResponse createTask(ForensicTaskRequest request, String actorUsername, String sourceIp) {
        EvidenceItem evidence = evidenceService.findEvidence(request.evidenceId());
        UserEntity assignee = resolveAssignee(request.assignedToUsername());

        ForensicTask task = forensicTaskRepository.save(ForensicTask.builder()
                .evidenceItem(evidence)
                .assignedTo(assignee)
                .taskName(request.taskName())
                .notes(request.notes())
                .priority(request.priority())
                .dueAt(request.dueAt())
                .build());

        auditService.record(actorUsername, "FORENSIC_TASK_CREATED", "FORENSIC_TASK", task.getId(),
                "Created forensic task for evidence " + evidence.getEvidenceNumber(), sourceIp);
        return toResponse(task);
    }

    @Transactional(readOnly = true)
    public List<ForensicTaskResponse> listTasks() {
        return forensicTaskRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ForensicTaskResponse completeTask(UUID taskId, CompleteTaskRequest request,
                                             String actorUsername, String sourceIp) {
        ForensicTask task = forensicTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Forensic task not found"));
        task.setStatus(ForensicTaskStatus.COMPLETED);
        task.setCompletedAt(Instant.now());
        if (StringUtils.hasText(request.notes())) {
            task.setNotes(request.notes());
        }

        auditService.record(actorUsername, "FORENSIC_TASK_COMPLETED", "FORENSIC_TASK", task.getId(),
                "Completed forensic task " + task.getTaskName(), sourceIp);
        return toResponse(task);
    }

    private UserEntity resolveAssignee(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
    }

    private ForensicTaskResponse toResponse(ForensicTask task) {
        String assignedToUsername = task.getAssignedTo() == null
                ? null
                : task.getAssignedTo().getUsername();
        return new ForensicTaskResponse(
                task.getId(),
                task.getEvidenceItem().getId(),
                task.getEvidenceItem().getEvidenceNumber(),
                assignedToUsername,
                task.getTaskName(),
                task.getNotes(),
                task.getStatus(),
                task.getPriority(),
                task.getDueAt(),
                task.getCompletedAt(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
