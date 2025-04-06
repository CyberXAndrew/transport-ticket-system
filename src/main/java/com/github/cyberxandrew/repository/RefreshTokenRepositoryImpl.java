package com.github.cyberxandrew.repository;

import com.github.cyberxandrew.model.RefreshToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    @Autowired private JdbcTemplate jdbcTemplate;

    @Override
    public Optional<RefreshToken> findByToken(String refreshToken) {
        String sql = "SELECT * FROM refresh_tokens WHERE token = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    sql, new Object[]{refreshToken}, new BeanPropertyRowMapper<>(RefreshToken.class)));
        } catch (DataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        UUID uuid = UUID.randomUUID();
        refreshToken.setId(uuid);

        String sql = "INSERT INTO refresh_tokens (id, user_id, token, expiry) VALUES (?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql, uuid, refreshToken.getUserId(), refreshToken.getToken(), Timestamp.from(refreshToken.getExpiry()));
        } catch (DataAccessException ex) {
            System.out.println(ex.getMessage());
        }
        return refreshToken;
    }

    @Override
    public void delete(UUID tokenId) {
        String sql = "DELETE FROM refresh_tokens WHERE id = ?";
        jdbcTemplate.update(sql, tokenId);
    }
}
