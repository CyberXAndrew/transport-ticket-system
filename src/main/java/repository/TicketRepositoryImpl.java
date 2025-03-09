package repository;

import dto.TicketDTO;
import mapper.TicketRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;

import model.Ticket;

public class TicketRepositoryImpl implements TicketRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TicketRowMapper ticketRowMapper;

    @Override
    public Ticket findById(Long ticketId) {
        String sql = "";
        return new Ticket();
    }

    @Override
    public List<Ticket> findByUserId(Long userId) {
        return List.of();
    }

    @Override
    public List<TicketDTO> findAll(Pageable pageable, LocalDateTime dateTime, String departurePoint,
                                   String destinationPoint, String carrierName) {
        String sql = "SELECT t.*, r.departure_point, r.destination_point, r.career_name FROM tickets t " +
                "JOIN routes r ON t.route_id = r.id WHERE t.user_id IS NULL";
        List<Object> paginationParams = new ArrayList<>();

        if (dateTime != null) {
            sql += " AND date_time = ?";
            paginationParams.add(dateTime);
        }
        if (departurePoint != null) {
            sql += " AND departure_point LIKE ?";
            paginationParams.add("%" + departurePoint + "%");
        }
        if (destinationPoint != null) {
            sql += " AND destination_point LIKE ?";
            paginationParams.add("%" + destinationPoint + "%");
        }
        if (carrierName != null) {
            sql += " AND carrier_name LIKE ?";
            paginationParams.add("%" + carrierName + "%");
        }
        if (pageable != null && pageable.isPaged()) {
            sql += " LIMIT ? OFFSET ?";
            int pageSize = pageable.getPageSize();
            long offset = pageable.getOffset();
            paginationParams.add(pageSize);
            paginationParams.add(offset);
        }

        return jdbcTemplate.query(sql, paginationParams.toArray(), ticketRowMapper);
    }

    @Override
    public List<TicketDTO> findAll() {
        return findAll(Pageable.unpaged(), null, null, null, null);
    }

    @Override
    public Ticket save(Ticket ticket) {
        return null;
    }

    @Override
    public Ticket update(Ticket ticket) {
        return null;
    }

    @Override
    public void deleteById(Long ticketId) {

    }

    @Override
    public boolean isTicketAvailable(Long ticketId) {
        return false;
    }
}
