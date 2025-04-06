package com.github.cyberxandrew.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
public class RefreshToken {
    private UUID id;
    @NotNull
    private Long userId;
    @NotBlank
    private String token;
    @NotNull
    private Instant expiry;

    public RefreshToken(Long userId, String token, Instant expiry) {
        this.userId = userId;
        this.token = token;
        this.expiry = expiry;
    }
}
