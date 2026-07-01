package com.evidentra.security;

import com.evidentra.config.SecurityProperties;
import com.evidentra.domain.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    @Test
    void generatedTokenCanBeValidated() {
        SecurityProperties properties = new SecurityProperties();
        properties.setIssuer("evidentra-test");
        properties.setExpiration(Duration.ofMinutes(30));
        properties.setSecret("test-only-secret-key-which-is-long-enough-for-hs256");
        JwtService jwtService = new JwtService(properties);
        UserDetails userDetails = User.withUsername("officer.one")
                .password("password")
                .roles(Role.OFFICER.name())
                .build();

        String token = jwtService.generateToken(userDetails, UUID.randomUUID(), Role.OFFICER);

        assertThat(jwtService.extractUsername(token)).isEqualTo("officer.one");
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }
}
