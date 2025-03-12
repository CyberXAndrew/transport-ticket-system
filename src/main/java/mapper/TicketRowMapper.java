package mapper;

import dto.TicketDTO;
import exception.ticket.TicketMappingException;
import model.Ticket;
import org.springframework.jdbc.core.RowMapper;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.TicketRepository;

public class TicketRowMapper implements RowMapper<TicketDTO> {

    private static final Logger logger = LoggerFactory.getLogger(TicketRepository.class);

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

    public Ticket mapTicket(ResultSet rs, int rowNum) throws SQLException {
        Ticket ticket = new Ticket();
        try {
            Long id = rs.getLong("id");
            if (rs.wasNull()) {
                logger.error("Ошибка при извлечении билета: id is NULL");
                throw new TicketMappingException("Error while mapping ticket: id is NULL");
            }
            ticket.setId(rs.getLong("id"));
            ticket.setDateTime(rs.getObject("date_time", LocalDateTime.class));
            Long userId = rs.getLong("user_id");
            ticket.setUserId(rs.wasNull() ? null : userId);
            Long routeId = rs.getLong("route_id");
            ticket.setRouteId(rs.wasNull() ? null : routeId);
            ticket.setPrice(rs.getBigDecimal("price"));
            ticket.setSeatNumber(StringUtils.defaultString(rs.getString("seat_number"), "Unknown"));
        } catch (SQLException ex) {
            logger.error("Ошибка при извлечении билета с id: {}", rs.getLong("id"), ex);
            throw new TicketMappingException("Error while mapping ticket with id: " + rs.getLong("id"), ex);
        }
        return ticket;
    }
}
