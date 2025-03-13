package com.github.cyberxandrew.mapper;

import com.github.cyberxandrew.dto.TicketDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class TicketDtoRowMapper implements RowMapper<TicketDTO> {
    @Override
    public TicketDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        TicketDTO ticketDTO = new TicketDTO();

        ticketDTO.setId(rs.getLong("id"));
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

        return ticketDTO;
    }
}
