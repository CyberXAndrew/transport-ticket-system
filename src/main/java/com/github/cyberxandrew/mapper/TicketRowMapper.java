package com.github.cyberxandrew.mapper;

import com.github.cyberxandrew.exception.ticket.TicketMappingException;
import com.github.cyberxandrew.model.Ticket;
import org.springframework.jdbc.core.RowMapper;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.cyberxandrew.repository.TicketRepository;
import org.springframework.stereotype.Component;

@Component
public class TicketRowMapper implements RowMapper<Ticket> {

    private static final Logger logger = LoggerFactory.getLogger(TicketRowMapper.class);

    @Override
    public Ticket mapRow(ResultSet rs, int rowNum) throws SQLException {
        Ticket ticket = new Ticket();
        long id = -1;
        try {
            id = rs.getLong("id");
            if (rs.wasNull()) {
                logger.error("Ошибка при извлечении билета: id is NULL");
                throw new TicketMappingException("Error while mapping ticket (to Ticket): id is NULL");
            }
            ticket.setId(id);
            ticket.setDateTime(rs.getObject("date_time", LocalDateTime.class));
            Long userId = rs.getLong("user_id");
            ticket.setUserId(rs.wasNull() ? null : userId);
            Long routeId = rs.getLong("route_id");
            ticket.setRouteId(rs.wasNull() ? null : routeId);
            ticket.setPrice(rs.getBigDecimal("price"));
            ticket.setSeatNumber(StringUtils.defaultString(rs.getString("seat_number"), "Unknown"));
        } catch (SQLException ex) {
            logger.error("Ошибка при извлечении билета с id: {}", id, ex);
            throw new TicketMappingException("Error while mapping ticket with id: " + id, ex);
        }
        return ticket;
    }
}
