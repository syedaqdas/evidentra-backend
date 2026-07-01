package com.evidentra.dto.user;

import com.evidentra.domain.enums.Role;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String fullName,
        Role role,
        boolean enabled,
        Instant createdAt,
        Instant updatedAt
) {
}
