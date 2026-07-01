package com.evidentra.repository;

import com.evidentra.domain.entity.ForensicTask;
import com.evidentra.domain.enums.ForensicTaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ForensicTaskRepository extends JpaRepository<ForensicTask, UUID> {

    List<ForensicTask> findByStatus(ForensicTaskStatus status);

    List<ForensicTask> findByEvidenceItemId(UUID evidenceItemId);
}
