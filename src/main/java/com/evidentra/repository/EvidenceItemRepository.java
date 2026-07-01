package com.evidentra.repository;

import com.evidentra.domain.entity.EvidenceItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvidenceItemRepository extends JpaRepository<EvidenceItem, UUID> {

    Optional<EvidenceItem> findByEvidenceNumber(String evidenceNumber);

    boolean existsByEvidenceNumber(String evidenceNumber);

    List<EvidenceItem> findByCaseRecordId(UUID caseRecordId);
}
