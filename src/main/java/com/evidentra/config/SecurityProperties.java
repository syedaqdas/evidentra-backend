package com.evidentra.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Data
@Validated
@ConfigurationProperties(prefix = "evidentra.security.jwt")
public class SecurityProperties {

    @NotBlank
    private String issuer = "evidentra";

    @NotBlank
    private String secret = "change-this-secret-key-before-running-evidentra";

    @NotNull
    private Duration expiration = Duration.ofHours(1);
}
