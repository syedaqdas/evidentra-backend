package com.evidentra.domain.entity;

import com.evidentra.domain.enums.ForensicTaskStatus;
import com.evidentra.domain.enums.Priority;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "forensic_tasks")
public class ForensicTask {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evidence_item_id", nullable = false)
    private EvidenceItem evidenceItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private UserEntity assignedTo;

    @Column(name = "task_name", nullable = false, length = 180)
    private String taskName;

    @Column(columnDefinition = "text")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ForensicTaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private Priority priority;

    @Column(name = "due_at")
    private Instant dueAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) {
            status = ForensicTaskStatus.PENDING;
        }
        if (priority == null) {
            priority = Priority.MEDIUM;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
