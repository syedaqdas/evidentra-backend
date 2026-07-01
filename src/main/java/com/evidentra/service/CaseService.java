package com.evidentra.service;

import com.evidentra.domain.entity.CaseRecord;
import com.evidentra.domain.entity.UserEntity;
import com.evidentra.dto.casefile.CaseCreateRequest;
import com.evidentra.dto.casefile.CaseResponse;
import com.evidentra.exception.ConflictException;
import com.evidentra.exception.ResourceNotFoundException;
import com.evidentra.repository.CaseRecordRepository;
import com.evidentra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaseService {

    private final CaseRecordRepository caseRecordRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    @Transactional
    public CaseResponse createCase(CaseCreateRequest request, String actorUsername, String sourceIp) {
        if (caseRecordRepository.existsByCaseNumber(request.caseNumber())) {
            throw new ConflictException("Case number already exists");
        }

        UserEntity leadOfficer = resolveLeadOfficer(request.leadOfficerUsername(), actorUsername);
        CaseRecord caseRecord = caseRecordRepository.save(CaseRecord.builder()
                .caseNumber(request.caseNumber())
                .title(request.title())
                .description(request.description())
                .leadOfficer(leadOfficer)
                .build());

        auditService.record(actorUsername, "CASE_CREATED", "CASE", caseRecord.getId(),
                "Created case " + caseRecord.getCaseNumber(), sourceIp);
        return toResponse(caseRecord);
    }

    @Transactional(readOnly = true)
    public List<CaseResponse> listCases() {
        return caseRecordRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CaseResponse getCase(UUID id) {
        return toResponse(findCase(id));
    }

    @Transactional(readOnly = true)
    public CaseRecord findCase(UUID id) {
        return caseRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));
    }

    private UserEntity resolveLeadOfficer(String requestedUsername, String actorUsername) {
        String username = StringUtils.hasText(requestedUsername) ? requestedUsername : actorUsername;
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Lead officer not found"));
    }

    private CaseResponse toResponse(CaseRecord caseRecord) {
        String leadOfficerUsername = caseRecord.getLeadOfficer() == null
                ? null
                : caseRecord.getLeadOfficer().getUsername();
        return new CaseResponse(
                caseRecord.getId(),
                caseRecord.getCaseNumber(),
                caseRecord.getTitle(),
                caseRecord.getDescription(),
                caseRecord.getStatus(),
                leadOfficerUsername,
                caseRecord.getOpenedAt(),
                caseRecord.getClosedAt(),
                caseRecord.getCreatedAt(),
                caseRecord.getUpdatedAt()
        );
    }
}
