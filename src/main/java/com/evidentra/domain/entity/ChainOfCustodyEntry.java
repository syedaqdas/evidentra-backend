package com.evidentra.domain.entity;

import com.evidentra.domain.enums.ChainOfCustodyAction;
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
@Table(name = "chain_of_custody_entries")
public class ChainOfCustodyEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evidence_item_id", nullable = false)
    private EvidenceItem evidenceItem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ChainOfCustodyAction action;

    @Column(name = "from_custodian", length = 160)
    private String fromCustodian;

    @Column(name = "to_custodian", nullable = false, length = 160)
    private String toCustodian;

    @Column(length = 180)
    private String location;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_id")
    private UserEntity recordedBy;

    @PrePersist
    void onCreate() {
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }
}
