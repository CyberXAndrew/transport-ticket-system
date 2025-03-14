package com.github.cyberxandrew.mapper;

import com.github.cyberxandrew.dto.TicketDTO;
import com.github.cyberxandrew.exception.ticket.TicketMappingException;
import com.github.cyberxandrew.repository.TicketRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class TicketDtoRowMapper implements RowMapper<TicketDTO> {
    private static final Logger logger = LoggerFactory.getLogger(TicketDtoRowMapper.class);
    @Override
    public TicketDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        TicketDTO ticketDTO = new TicketDTO();
        long id = -1;
        try {
            id = rs.getLong("id");
            if (rs.wasNull()) {
                logger.error("Ошибка при извлечении билета: id is NULL");
                throw new TicketMappingException("Error while mapping ticket (to DTO): id is NULL");
            }
            ticketDTO.setId(id);
            ticketDTO.setDateTime(rs.getObject("date_time", LocalDateTime.class));
            ticketDTO.setUserId(rs.getLong("user_id"));
            ticketDTO.setRouteId(rs.getLong("route_id"));
            ticketDTO.setPrice(rs.getBigDecimal("price"));
            ticketDTO.setSeatNumber(StringUtils.defaultString(rs.getString("seat_number"), "Unknown"));
            ticketDTO.setDeparturePoint(StringUtils.defaultString(rs.getString("departure_point"),
                    "Unknown"));
            ticketDTO.setDestinationPoint(StringUtils.defaultString(rs.getString("destination_point"),
                    "Unknown"));
            ticketDTO.setCarrierName(StringUtils.defaultString(rs.getString("carrier_name"),
                    "Unknown"));
        } catch (SQLException ex) {
            logger.error("Ошибка при извлечении билета с id: {}", id, ex);
            throw new TicketMappingException("Error while mapping ticket with id: " + id, ex);
        }
        return ticketDTO;
    }
}
