package com.evidentra.repository;

import com.evidentra.domain.entity.ChainOfCustodyEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChainOfCustodyEntryRepository extends JpaRepository<ChainOfCustodyEntry, UUID> {

    List<ChainOfCustodyEntry> findByEvidenceItemIdOrderByOccurredAtAsc(UUID evidenceItemId);
}
