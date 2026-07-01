package com.evidentra.service;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HashingServiceTest {

    private final HashingService hashingService = new HashingService();

    @Test
    void sha256HexReturnsExpectedDigest() {
        String digest = hashingService.sha256Hex("evidentra".getBytes(StandardCharsets.UTF_8));

        assertThat(digest).isEqualTo("5ed0a33ec6abe6be62952ec91bbe8270800bfe17eb94f8ec02e746b5c568cfee");
    }
}
