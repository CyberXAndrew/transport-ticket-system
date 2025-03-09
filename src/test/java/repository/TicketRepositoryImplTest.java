package repository;

import dto.TicketDTO;
import mapper.TicketRowMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TicketRepositoryImplTest {
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private TicketRowMapper ticketRowMapper;
    @InjectMocks
    private TicketRepositoryImpl ticketRepository;

    @Test
    public void findAllTest() {
        Pageable pageable = Pageable.unpaged();
        LocalDateTime dateTime = LocalDateTime.now();
        BigDecimal price = new BigDecimal("10.0");
        String departurePoint = "Saints-Petersburg";
        String destinationPoint = "Moscow";
        String carrierName = "Java Airlines";

        String expectedSql = "SELECT t.*, r.departure_point, r.destination_point, r.carrier_name" +
                " FROM tickets t JOIN routes r ON t.route_id = r.id WHERE t.user_id IS NULL" +
                " AND date_time = ? AND departure_point LIKE ? AND destination_point LIKE ? " +
                "AND carrier_name LIKE ? LIMIT ? OFFSET ?";
        Object[] expectedParams = new Object[] {dateTime, "%" + departurePoint + "%", "%" + destinationPoint + "%",
                "%" + carrierName + "%", pageable.getPageSize(), pageable.getOffset()};
        TicketDTO ticketDTO = new TicketDTO(1L, dateTime, null, null, price, "1A",
                departurePoint, destinationPoint, carrierName);
        List<TicketDTO> expectedList = Collections.singletonList(ticketDTO);

        when(jdbcTemplate.query(eq(expectedSql), eq(expectedParams), any(TicketRowMapper.class)))
                .thenReturn(expectedList);

        List<TicketDTO> actualList = ticketRepository.findAll(pageable, dateTime, departurePoint,
                destinationPoint, carrierName);
        assertEquals(expectedList, actualList);
        verify(jdbcTemplate).query(eq(expectedSql), eq(expectedParams), any(TicketRowMapper.class));
    }

    @Test
    public void findAllTestWithoutParams() {
        String expectedSql = "SELECT t.*, r.departure_point, r.destination_point, r.carrier_name" +
                " FROM tickets t JOIN routes r ON t.route_id = r.id WHERE t.user_id IS NULL";
        Object[] expectedParams = new Object[0];
        TicketDTO ticketDTO = new TicketDTO(1L, LocalDateTime.of(2019,02, 22,
                9, 40, 00), 1L, 1L, BigDecimal.valueOf(10), "1A",
                "Paris","Vienna", "Java Airlines");
        List<TicketDTO> expectedList = Collections.singletonList(ticketDTO);

        when(jdbcTemplate.query(eq(expectedSql), eq(expectedParams), any(TicketRowMapper.class)))
                .thenReturn(expectedList);

        List<TicketDTO> actualList = ticketRepository.findAll();
        assertEquals(expectedList, actualList);
        verify(jdbcTemplate).query(eq(expectedSql), eq(expectedParams), any(TicketRowMapper.class));
    }
}
