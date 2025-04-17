package com.github.cyberxandrew.repository;

import com.github.cyberxandrew.exception.user.UserDeletionException;
import com.github.cyberxandrew.exception.user.UserHasTicketsException;
import com.github.cyberxandrew.exception.user.UserNotFoundException;
import com.github.cyberxandrew.exception.user.UserSaveException;
import com.github.cyberxandrew.exception.user.UserUpdateException;
import com.github.cyberxandrew.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);
    private final RowMapper<User> userRowMapper = new BeanPropertyRowMapper<>(User.class);
    @Autowired private JdbcTemplate jdbcTemplate;


    @Override
    @Transactional
    public Optional<User> findById(Long userId) {
        if (userId == null) throw new NullPointerException("User with id = null cannot be found in database");
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new Object[]{userId}, userRowMapper));
        } catch (EmptyResultDataAccessException ex) {
            logger.warn("User with id {} not found", userId);
            throw new UserNotFoundException("User was not found", ex);
        }
    }

    @Override
    @Transactional
    public Optional<User> findByLogin(String login) { //Fix No test
        String sql = "SELECT * FROM users WHERE login = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new Object[]{login}, userRowMapper));
        } catch (DataAccessException ex) {
            logger.error("User with login: {} not found", login);
            throw new UserNotFoundException("Error while getting user by login", ex);
        }
    }

    @Override
    @Transactional
    public boolean existsByLogin(String login) { //Fix No test
        String sql = "SELECT EXISTS (SELECT * FROM users WHERE login = ?)";
        try {
            return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, new SingleColumnRowMapper<>(Boolean.class), login));
        } catch (DataAccessException ex) {
            logger.error("Error while getting user with login: {}", login);
            throw new UserNotFoundException("Error while getting user by login", ex);
        }
    }

    @Override
    @Transactional
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    @Transactional
    public User save(User user) {
        String sql = "INSERT INTO users (login, password, full_name, role)" +
                " VALUES (?, ?, ?, ?)";
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
                preparedStatement.setString(1, user.getLogin());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.setString(3, user.getFullName());
                preparedStatement.setString(4, user.getRole().toUpperCase());
                return preparedStatement;
            }, keyHolder);

            user.setId(keyHolder.getKey().longValue());
            logger.debug("User with id: {} successfully created", user.getId());
            return user;
        } catch (NullPointerException | DataAccessException ex) {
            logger.error("Error while saving user: {}", user.toString());
            throw new UserSaveException("Error while saving user", ex);
        }
    }

    @Override
    @Transactional
    public User update(User user) {
        try {
            String sql = "UPDATE users SET login = ?, password = ?, full_name = ?, role = ?" +
                    " WHERE id = ?";
            int updated = jdbcTemplate.update(sql, user.getLogin(), user.getPassword(),
                    user.getFullName(), user.getRole().toUpperCase(), user.getId());
            if (updated > 0) {
                logger.debug("Updating user with id: {} is successful", user.getId());
            } else {
                logger.warn("User with id: {} not found for updating", user.getId());
                throw new UserUpdateException("User not found for updating");
            }
            return user;
        } catch (DataAccessException ex) {
            logger.error("Error while updating user with id: {}", user.getId());
            throw new UserUpdateException("Error while updating user", ex);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {
        if (hasTicketsBounded(userId)) throw new UserHasTicketsException("Not possible to delete a user" +
                " with id: " + userId + " because it is referenced in the tickets table");
        try {
            Optional<User> user = findById(userId);
            if (user.isEmpty()) {
                logger.warn("User with id: {} cannot be mapped from database", userId);
                throw new UserNotFoundException("User cannot be mapped from database while deletion");
            }
            String sql = "DELETE FROM users WHERE id = ?";
            jdbcTemplate.update(sql, userId);
            logger.debug("User with id {} successfully deleted", userId);
        } catch (DataAccessException ex) {
            logger.error("Error when deleting user with id = {}: {}", userId, ex.getMessage(), ex);
            throw new UserDeletionException("Error when deleting user", ex);
        }
    }

    public boolean hasTicketsBounded(Long userId) {
        String sql = "SELECT COUNT(*) FROM tickets WHERE user_id = ?";
        Integer i = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return i != null && i > 0;
    }
}
