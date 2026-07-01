package com.evidentra.repository;

import com.evidentra.domain.entity.CaseRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CaseRecordRepository extends JpaRepository<CaseRecord, UUID> {

    Optional<CaseRecord> findByCaseNumber(String caseNumber);

    boolean existsByCaseNumber(String caseNumber);
}
