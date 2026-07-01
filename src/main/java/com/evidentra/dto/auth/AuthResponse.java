package com.evidentra.dto.auth;

import com.evidentra.domain.enums.Role;

import java.time.Instant;

public record AuthResponse(
        String tokenType,
        String accessToken,
        Instant expiresAt,
        String username,
        Role role
) {
}
