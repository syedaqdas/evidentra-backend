package com.evidentra.service;

import com.evidentra.domain.entity.CaseRecord;
import com.evidentra.domain.entity.ChainOfCustodyEntry;
import com.evidentra.domain.entity.EvidenceItem;
import com.evidentra.domain.entity.UserEntity;
import com.evidentra.domain.enums.ChainOfCustodyAction;
import com.evidentra.domain.enums.EvidenceStatus;
import com.evidentra.dto.evidence.CustodyEntryResponse;
import com.evidentra.dto.evidence.CustodyTransferRequest;
import com.evidentra.dto.evidence.EvidenceCreateRequest;
import com.evidentra.dto.evidence.EvidenceResponse;
import com.evidentra.dto.evidence.IntegrityVerifyResponse;
import com.evidentra.exception.BadRequestException;
import com.evidentra.exception.ConflictException;
import com.evidentra.exception.ResourceNotFoundException;
import com.evidentra.repository.ChainOfCustodyEntryRepository;
import com.evidentra.repository.EvidenceItemRepository;
import com.evidentra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EvidenceService {

    private final EvidenceItemRepository evidenceItemRepository;
    private final ChainOfCustodyEntryRepository custodyEntryRepository;
    private final UserRepository userRepository;
    private final CaseService caseService;
    private final AuditService auditService;

    @Transactional
    public EvidenceResponse createEvidence(EvidenceCreateRequest request, String actorUsername, String sourceIp) {
        if (evidenceItemRepository.existsByEvidenceNumber(request.evidenceNumber())) {
            throw new ConflictException("Evidence number already exists");
        }

        CaseRecord caseRecord = caseService.findCase(request.caseId());
        UserEntity actor = findUser(actorUsername);
        EvidenceItem evidence = evidenceItemRepository.save(EvidenceItem.builder()
                .caseRecord(caseRecord)
                .evidenceNumber(request.evidenceNumber())
                .type(request.type())
                .status(EvidenceStatus.COLLECTED)
                .description(request.description())
                .storageLocation(request.storageLocation())
                .fileName(request.fileName())
                .contentType(request.contentType())
                .sha256Hash(normalizeHash(request.sha256Hash()))
                .sizeBytes(request.sizeBytes())
                .collectedBy(actor)
                .build());

        custodyEntryRepository.save(ChainOfCustodyEntry.builder()
                .evidenceItem(evidence)
                .action(ChainOfCustodyAction.COLLECTED)
                .toCustodian(actor.getUsername())
                .location(request.storageLocation())
                .notes("Initial collection")
                .recordedBy(actor)
                .build());

        auditService.record(actorUsername, "EVIDENCE_CREATED", "EVIDENCE", evidence.getId(),
                "Created evidence " + evidence.getEvidenceNumber(), sourceIp);
        return toResponse(evidence);
    }

    @Transactional(readOnly = true)
    public EvidenceResponse getEvidence(UUID id) {
        return toResponse(findEvidence(id));
    }

    @Transactional(readOnly = true)
    public List<EvidenceResponse> listEvidenceByCase(UUID caseId) {
        return evidenceItemRepository.findByCaseRecordId(caseId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public IntegrityVerifyResponse verifyIntegrity(UUID evidenceId, String providedHash,
                                                   String actorUsername, String sourceIp) {
        EvidenceItem evidence = findEvidence(evidenceId);
        if (evidence.getSha256Hash() == null) {
            throw new BadRequestException("Evidence does not have a stored SHA-256 hash");
        }

        String normalizedProvidedHash = normalizeHash(providedHash);
        boolean verified = evidence.getSha256Hash().equals(normalizedProvidedHash);
        auditService.record(actorUsername, verified ? "EVIDENCE_INTEGRITY_VERIFIED" : "EVIDENCE_INTEGRITY_FAILED",
                "EVIDENCE", evidence.getId(), "Integrity verification for " + evidence.getEvidenceNumber(), sourceIp);

        return new IntegrityVerifyResponse(
                evidence.getId(),
                evidence.getEvidenceNumber(),
                evidence.getSha256Hash(),
                normalizedProvidedHash,
                verified,
                Instant.now()
        );
    }

    @Transactional
    public CustodyEntryResponse transferCustody(UUID evidenceId, CustodyTransferRequest request,
                                                String actorUsername, String sourceIp) {
        EvidenceItem evidence = findEvidence(evidenceId);
        UserEntity actor = findUser(actorUsername);

        EvidenceStatus newStatus = request.newStatus() == null ? EvidenceStatus.TRANSFERRED : request.newStatus();
        evidence.setStatus(newStatus);

        ChainOfCustodyEntry entry = custodyEntryRepository.save(ChainOfCustodyEntry.builder()
                .evidenceItem(evidence)
                .action(request.action() == null ? ChainOfCustodyAction.TRANSFERRED : request.action())
                .fromCustodian(request.fromCustodian())
                .toCustodian(request.toCustodian())
                .location(request.location())
                .notes(request.notes())
                .recordedBy(actor)
                .build());

        auditService.record(actorUsername, "CUSTODY_TRANSFER_RECORDED", "EVIDENCE", evidence.getId(),
                "Recorded custody event for " + evidence.getEvidenceNumber(), sourceIp);
        return toCustodyResponse(entry);
    }

    @Transactional(readOnly = true)
    public List<CustodyEntryResponse> listCustody(UUID evidenceId) {
        if (!evidenceItemRepository.existsById(evidenceId)) {
            throw new ResourceNotFoundException("Evidence not found");
        }
        return custodyEntryRepository.findByEvidenceItemIdOrderByOccurredAtAsc(evidenceId).stream()
                .map(this::toCustodyResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EvidenceItem findEvidence(UUID id) {
        return evidenceItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evidence not found"));
    }

    private UserEntity findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private String normalizeHash(String hash) {
        return hash == null ? null : hash.toLowerCase(Locale.ROOT);
    }

    private EvidenceResponse toResponse(EvidenceItem evidence) {
        String collectedByUsername = evidence.getCollectedBy() == null
                ? null
                : evidence.getCollectedBy().getUsername();
        return new EvidenceResponse(
                evidence.getId(),
                evidence.getEvidenceNumber(),
                evidence.getCaseRecord().getId(),
                evidence.getCaseRecord().getCaseNumber(),
                evidence.getType(),
                evidence.getStatus(),
                evidence.getDescription(),
                evidence.getStorageLocation(),
                evidence.getFileName(),
                evidence.getContentType(),
                evidence.getSha256Hash(),
                evidence.getSizeBytes(),
                evidence.getCollectedAt(),
                collectedByUsername,
                evidence.getCreatedAt(),
                evidence.getUpdatedAt()
        );
    }

    private CustodyEntryResponse toCustodyResponse(ChainOfCustodyEntry entry) {
        String recordedByUsername = entry.getRecordedBy() == null
                ? null
                : entry.getRecordedBy().getUsername();
        return new CustodyEntryResponse(
                entry.getId(),
                entry.getEvidenceItem().getId(),
                entry.getAction(),
                entry.getFromCustodian(),
                entry.getToCustodian(),
                entry.getLocation(),
                entry.getNotes(),
                entry.getOccurredAt(),
                recordedByUsername
        );
    }
}
