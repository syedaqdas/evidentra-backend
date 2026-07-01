package com.evidentra.service;

import com.evidentra.domain.entity.UserEntity;
import com.evidentra.domain.enums.Role;
import com.evidentra.dto.auth.AuthRequest;
import com.evidentra.dto.auth.AuthResponse;
import com.evidentra.dto.auth.RegisterRequest;
import com.evidentra.exception.BadRequestException;
import com.evidentra.exception.ConflictException;
import com.evidentra.repository.UserRepository;
import com.evidentra.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuditService auditService;

    @Transactional
    public AuthResponse register(RegisterRequest request, String sourceIp) {
        if (userRepository.count() > 0) {
            throw new ConflictException("Bootstrap registration is already complete; administrators must create users");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username already exists");
        }
        if (request.role() != null && request.role() != Role.ADMIN) {
            throw new BadRequestException("The bootstrap user must be an ADMIN");
        }

        Role role = Role.ADMIN;
        UserEntity user = userRepository.save(UserEntity.builder()
                .username(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .role(role)
                .enabled(true)
                .build());

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails, user.getId(), user.getRole());
        auditService.record(user.getUsername(), "USER_REGISTERED", "USER", user.getId(),
                "User registered with role " + role, sourceIp);

        return new AuthResponse("Bearer", token, jwtService.getExpirationInstant(), user.getUsername(), user.getRole());
    }

    @Transactional(readOnly = true)
    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.username(),
                request.password()
        ));

        UserEntity user = userRepository.findByUsername(request.username())
                .orElseThrow();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails, user.getId(), user.getRole());
        return new AuthResponse("Bearer", token, jwtService.getExpirationInstant(), user.getUsername(), user.getRole());
    }
}
