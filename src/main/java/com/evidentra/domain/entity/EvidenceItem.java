package com.evidentra.domain.entity;

import com.evidentra.domain.enums.EvidenceStatus;
import com.evidentra.domain.enums.EvidenceType;
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
import jakarta.persistence.Version;
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
@Table(name = "evidence_items")
public class EvidenceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "evidence_number", nullable = false, unique = true, length = 80)
    private String evidenceNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_record_id", nullable = false)
    private CaseRecord caseRecord;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private EvidenceType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private EvidenceStatus status;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "storage_location", length = 180)
    private String storageLocation;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "content_type", length = 120)
    private String contentType;

    @Column(name = "sha256_hash", length = 64)
    private String sha256Hash;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(name = "collected_at", nullable = false)
    private Instant collectedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collected_by_id")
    private UserEntity collectedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private Long version;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (collectedAt == null) {
            collectedAt = now;
        }
        if (status == null) {
            status = EvidenceStatus.COLLECTED;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
