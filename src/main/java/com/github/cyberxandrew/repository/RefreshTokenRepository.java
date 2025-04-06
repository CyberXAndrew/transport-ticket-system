package com.github.cyberxandrew.repository;

import com.github.cyberxandrew.model.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
    Optional<RefreshToken> findByToken(String refreshToken);
    RefreshToken save(RefreshToken refreshToken);
    void delete(UUID tokenId);
}
