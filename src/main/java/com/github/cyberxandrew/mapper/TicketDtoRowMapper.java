package com.github.cyberxandrew.mapper;

import com.github.cyberxandrew.dto.TicketWithRouteDataDTO;
import com.github.cyberxandrew.exception.ticket.TicketMappingException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class TicketDtoRowMapper implements RowMapper<TicketWithRouteDataDTO> {
    private static final Logger logger = LoggerFactory.getLogger(TicketDtoRowMapper.class);
    @Override
    public TicketWithRouteDataDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        TicketWithRouteDataDTO ticketWithRouteDataDTO = new TicketWithRouteDataDTO();
        long id = -1;
        try {
            id = rs.getLong("id");
            if (rs.wasNull()) {
                logger.error("Ошибка при извлечении билета: id is NULL");
                throw new TicketMappingException("Error while mapping ticket (to DTO): id is NULL");
            }
            ticketWithRouteDataDTO.setId(id);
            ticketWithRouteDataDTO.setDateTime(rs.getObject("date_time", LocalDateTime.class));
            ticketWithRouteDataDTO.setUserId(rs.getLong("user_id"));
            ticketWithRouteDataDTO.setRouteId(rs.getLong("route_id"));
            ticketWithRouteDataDTO.setPrice(rs.getBigDecimal("price"));
            ticketWithRouteDataDTO.setSeatNumber(StringUtils.defaultString(rs.getString("seat_number"), "Unknown"));
            ticketWithRouteDataDTO.setDeparturePoint(StringUtils.defaultString(rs.getString("departure_point"),
                    "Unknown"));
            ticketWithRouteDataDTO.setDestinationPoint(StringUtils.defaultString(rs.getString("destination_point"),
                    "Unknown"));
            ticketWithRouteDataDTO.setCarrierName(StringUtils.defaultString(rs.getString("carrier_name"),
                    "Unknown"));
        } catch (SQLException ex) {
            logger.error("Ошибка при извлечении билета с id: {}", id, ex);
            throw new TicketMappingException("Error while mapping ticket with id: " + id, ex);
        }
        return ticketWithRouteDataDTO;
    }
}
