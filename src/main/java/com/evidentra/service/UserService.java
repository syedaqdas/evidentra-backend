package com.evidentra.service;

import com.evidentra.domain.entity.UserEntity;
import com.evidentra.dto.user.UserCreateRequest;
import com.evidentra.dto.user.UserResponse;
import com.evidentra.exception.ConflictException;
import com.evidentra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    @Transactional
    public UserResponse createUser(UserCreateRequest request, String actorUsername, String sourceIp) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username already exists");
        }

        UserEntity user = userRepository.save(UserEntity.builder()
                .username(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .role(request.role())
                .enabled(true)
                .build());

        auditService.record(actorUsername, "USER_CREATED", "USER", user.getId(),
                "Created user " + user.getUsername(), sourceIp);
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private UserResponse toResponse(UserEntity user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole(),
                user.isEnabled(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
