package com.github.cyberxandrew.repository;

import com.github.cyberxandrew.mapper.TicketDtoRowMapper;
import com.github.cyberxandrew.model.Ticket;
import com.github.cyberxandrew.dto.TicketDTO;
import com.github.cyberxandrew.mapper.TicketRowMapper;
import com.github.cyberxandrew.exception.ticket.TicketAvailabilityException;
import com.github.cyberxandrew.exception.ticket.TicketDeletionException;
import com.github.cyberxandrew.exception.ticket.TicketNotFoundException;
import com.github.cyberxandrew.exception.ticket.TicketSaveException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Repository
public class TicketRepositoryImpl implements TicketRepository {
    private static final Logger logger = LoggerFactory.getLogger(TicketRepositoryImpl.class);
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private TicketRowMapper ticketRowMapper;
    @Autowired private TicketDtoRowMapper ticketDtoRowMapper;

    @Override
    public Optional<Ticket> findById(Long ticketId) {
        if (ticketId == null) throw new NullPointerException("Ticket with id = null cannot be found in database");
        String sql = "SELECT * FROM tickets WHERE id = ?";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, new Object[]{ticketId}, ticketRowMapper));
        } catch (EmptyResultDataAccessException ex) {
            logger.warn("Ticket with id {} not found", ticketId);
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findByUserId(Long userId) {
        String sql = "SELECT * FROM tickets WHERE user_id = ?";
        List<Ticket> result = jdbcTemplate.query(sql, new Object[]{userId}, ticketRowMapper);
        logger.debug("Found {} tickets for user with id: {}", result.size(), userId);
        return result;
    }

    @Override
    @Transactional
    public List<TicketDTO> findAll(Pageable pageable, LocalDateTime dateTime, String departurePoint,
                                   String destinationPoint, String carrierName) {
        StringBuilder sql = new StringBuilder("SELECT t.*, r.departure_point, r.destination_point, r.carrier_name" +
                " FROM tickets t JOIN routes r ON t.route_id = r.id WHERE t.user_id IS NULL");
        List<Object> filtrationParams = new ArrayList<>();

        addFilter(sql, filtrationParams, "date_time", dateTime);
        addFilter(sql, filtrationParams, "departure_point", departurePoint);
        addFilter(sql, filtrationParams, "destination_point", destinationPoint);
        addFilter(sql, filtrationParams, "carrier_name", carrierName);
        if (pageable != null && pageable.isPaged()) {
            sql.append(" LIMIT ? OFFSET ?");
            int pageSize = pageable.getPageSize();
            long offset = pageable.getOffset();
            filtrationParams.add(pageSize);
            filtrationParams.add(offset);
        }

        List<TicketDTO> query = jdbcTemplate.query(sql.toString(), filtrationParams.toArray(), ticketDtoRowMapper);
        return query;
    }

    private void addFilter(StringBuilder sql, List<Object> filtrationParams, String columnName, Object filter) {
        if (filter != null) {
            if (filter instanceof String) {
                sql.append(" AND ").append(columnName).append(" LIKE ?");
                filtrationParams.add("%" + filter + "%");
            } else {
                sql.append(" AND ").append(columnName).append(" = ?");
                filtrationParams.add(filter);
            }
        }
    }

    public List<TicketDTO> findAll() {
        return findAll(Pageable.unpaged(), null, null,
                null, null);
    }

    @Override
    @Transactional
    public Ticket save(Ticket ticket) {
        try {
            if (ticket.getId() == null) {
                String sql = "INSERT INTO tickets (date_time, user_id, route_id, price, seat_number) " +
                        "VALUES (?, ?, ?, ?, ?)";
                KeyHolder keyHolder = new GeneratedKeyHolder();

                jdbcTemplate.update(connection -> {
                    PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
                    preparedStatement.setObject(1, ticket.getDateTime());
                    preparedStatement.setObject(2, ticket.getUserId());
                    preparedStatement.setObject(3, ticket.getRouteId());
                    preparedStatement.setBigDecimal(4, ticket.getPrice());
                    preparedStatement.setString(5, ticket.getSeatNumber());
                    return preparedStatement;
                }, keyHolder);

                ticket.setId(keyHolder.getKey().longValue());
                logger.debug("Ticket with id: {} successfully created", ticket.getId());
                return ticket;
            } else {
                String sql = "UPDATE tickets SET date_time = ?, user_id = ?, route_id = ?, price = ?, seat_number = ?" +
                        " WHERE id = ?";

                int updated = jdbcTemplate.update(sql,
                        ticket.getDateTime(),
                        ticket.getUserId(),
                        ticket.getRouteId(),
                        ticket.getPrice(),
                        ticket.getSeatNumber(),
                        ticket.getId());
                if (updated > 0) {
                    logger.debug("Updating ticket with id: {} is successful", ticket.getId());
                } else {
                    logger.warn("Ticket with id: {} not found for updating", ticket.getId());
                    throw new TicketSaveException("Ticket not found for updating");
                }
                return ticket;
            }
        } catch (DataAccessException ex) {
            logger.error("Error while saving/updating ticket with id: {}", ticket.getId());
            throw new TicketSaveException("Error while saving/updating ticket", ex);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long ticketId) {
        try {
            Optional<Ticket> ticket = findById(ticketId);
            if (ticket.isEmpty()) {
                logger.warn("Ticket with id: {} not found", ticketId);
                throw new TicketNotFoundException("Ticket not found while deletion");
            }
            String sql = "DELETE FROM tickets WHERE id = ?";
            jdbcTemplate.update(sql, ticketId);
            logger.debug("Ticket with id {} successfully deleted", ticketId);
        } catch (DataAccessException ex) {
            logger.error("Error when deleting a ticket with id = {}: {}", ticketId, ex.getMessage(), ex);
            throw new TicketDeletionException("Error when deleting a ticket", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTicketAvailable(Long ticketId) {
        String sql = "SELECT EXISTS (SELECT 1 FROM tickets WHERE id = ? AND user_id IS NULL)";
        try {
            Boolean result = jdbcTemplate.queryForObject(sql, new Object[]{ticketId}, Boolean.class);
            logger.debug("Ticket with id: {} is available: {}", ticketId, result);
            return result;
        } catch (EmptyResultDataAccessException ex) {
            logger.error("Ticket with id: {} availability definition error", ticketId, ex);
            throw new TicketAvailabilityException("Ticket availability definition error", ex);
        }
    }
}
