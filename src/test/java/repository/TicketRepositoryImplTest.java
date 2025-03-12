package repository;

import model.Ticket;
import mapper.TicketRowMapper;
import exception.ticket.TicketAvailabilityException;
import exception.ticket.TicketDeleteException;
import exception.ticket.TicketNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Collections;


import java.sql.SQLException;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class TicketRepositoryImplTest {
    @Mock
    private Logger logger;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private TicketRowMapper ticketRowMapper;
    @InjectMocks
    private TicketRepositoryImpl ticketRepository;

    private Long testUserId;
    private Long testTicketId;
    private Ticket testTicket;
    @BeforeEach
    void beforeEach() {
        testUserId = 1L;
        testTicketId = 1L;
        testTicket = new Ticket();
        testTicket.setId(testTicketId);
    }

    @Test
    public void testFindByIdSuccessful() {
        String sql = "SELECT * FROM tickets WHERE id = ?";

        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{testTicketId}), ticketRowMapper::mapTicket))
                .thenReturn(testTicket);

        Optional<Ticket> actual = ticketRepository.findById(testTicketId);
        assertTrue(actual.isPresent());
        assertEquals(actual.get(), testTicket);
    }
    @Test
    public void testFindByIdFailed() {
        String sql = "SELECT * FROM tickets WHERE id = ?";

        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{testTicketId}), ticketRowMapper::mapTicket))
                .thenThrow(new EmptyResultDataAccessException(1));

        Optional<Ticket> actual = ticketRepository.findById(testTicketId);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testFindByUserIdSuccessful() {
        String sql = "SELECT * FROM tickets WHERE user_id = ?";
        testTicket.setUserId(testUserId);

        Ticket secondTestTicket = new Ticket();
        secondTestTicket.setId(2L);
        secondTestTicket.setUserId(testUserId);

        List<Ticket> tickets = new ArrayList<>();
        Collections.addAll(tickets, testTicket, secondTestTicket);

        when(jdbcTemplate.query(eq(sql), eq(new Object[]{testUserId}), ticketRowMapper::mapTicket))
                .thenReturn(tickets);

        List<Ticket> result = ticketRepository.findByUserId(testUserId);
        assertFalse(result.isEmpty());
        assertTrue(result.containsAll(Arrays.asList(testTicket, secondTestTicket)));
    }
    @Test
    public void testFindByUserIdFailed() {
        String sql = "SELECT * FROM tickets WHERE user_id = ?";
        testTicket.setUserId(testUserId);

        when(jdbcTemplate.query(eq(sql), eq(new Object[]{testUserId}), ticketRowMapper::mapTicket))
                .thenReturn(Collections.emptyList());

        List<Ticket> result = ticketRepository.findByUserId(testUserId);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testDeleteByIdSuccessful() {
        String sql = "DELETE FROM tickets WHERE id = ?";

        when(ticketRepository.findById(testTicketId)).thenReturn(Optional.of(testTicket));

        doNothing().when(jdbcTemplate).update(eq(sql), eq(testTicketId));

        ticketRepository.deleteById(testTicketId);

        verify(jdbcTemplate).update(eq(sql), eq(testTicketId));
        verify(ticketRepository, times(1)).findById(testTicketId);
    }
    @Test
    public void testDeleteByIdFailed() {
        String sql = "DELETE FROM tickets WHERE id = ?";

        when(ticketRepository.findById(testTicketId)).thenReturn(Optional.empty());
        assertThrows(TicketNotFoundException.class,
                () -> {ticketRepository.deleteById(testTicketId);});
    }
    @Test
    public void testDeleteByIdDataBaseError() {
        String sql = "DELETE FROM tickets WHERE id = ?";

        when(ticketRepository.findById(testTicketId)).thenReturn(Optional.of(testTicket));
        doThrow(new EmptyResultDataAccessException(1)).when(jdbcTemplate).update(eq(sql), eq(testTicketId));

        assertThrows(TicketDeleteException.class, () -> {
            ticketRepository.deleteById(testTicketId);
        });
    }

    @Test
    public void testIsTicketAvailableTrue() {
        String sql = "SELECT EXISTS (SELECT 1 FROM tickets WHERE id = ? AND user_id IS NULL)";

        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{testTicketId}), Boolean.class))
                .thenReturn(Boolean.TRUE);

        boolean result = ticketRepository.isTicketAvailable(testTicketId);
        assertTrue(result);
    }
    @Test
    public void testIsTicketAvailableFalse() {
        String sql = "SELECT EXISTS (SELECT 1 FROM tickets WHERE id = ? AND user_id IS NULL)";

        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{testTicketId}), Boolean.class))
                .thenReturn(Boolean.FALSE);

        boolean result = ticketRepository.isTicketAvailable(testTicketId);
        assertFalse(result);
    }
    @Test
    public void testIsTicketAvailableDoesNotExists() {
        String sql = "SELECT EXISTS (SELECT 1 FROM tickets WHERE id = ? AND user_id IS NULL)";

        doThrow(new EmptyResultDataAccessException(1)).when(jdbcTemplate)
                .queryForObject(eq(sql), eq(new Object[]{testTicketId}), Boolean.class);

        assertThrows(TicketAvailabilityException.class, () -> {
           ticketRepository.isTicketAvailable(testTicketId);
        });
    }

    @Test
    public void testSaveSuccessfulUpdate() {
        String sql = "UPDATE tickets SET date_time = ?, user_id = ?, route_id = ?, price = ?, seat_number = ?" +
                " WHERE id = ?";

        when(logger.isDebugEnabled()).thenReturn(true);
        when(jdbcTemplate.update(eq(sql), any(LocalDateTime.class), anyLong(), anyLong(),
                any(BigInteger.class), anyString(), anyLong())).thenReturn(1);

        Ticket updatedTicket = ticketRepository.save(testTicket);

        assertEquals(testTicket.getId(), updatedTicket.getId());

        verify(logger, times(1))
                .debug("Updating ticket with id: {} is successful", testTicket.getId());
    }











//    @Test
//    public void findAllTest() {
//        Pageable pageable = Pageable.unpaged();
//        LocalDateTime dateTime = LocalDateTime.now();
//        BigDecimal price = new BigDecimal("10.0");
//        String departurePoint = "Saints-Petersburg";
//        String destinationPoint = "Moscow";
//        String carrierName = "Java Airlines";
//
//        String expectedSql = "SELECT t.*, r.departure_point, r.destination_point, r.carrier_name" +
//                " FROM tickets t JOIN routes r ON t.route_id = r.id WHERE t.user_id IS NULL" +
//                " AND date_time = ? AND departure_point LIKE ? AND destination_point LIKE ? " +
//                "AND carrier_name LIKE ? LIMIT ? OFFSET ?";
//        Object[] expectedParams = new Object[] {dateTime, "%" + departurePoint + "%", "%" + destinationPoint + "%",
//                "%" + carrierName + "%", pageable.getPageSize(), pageable.getOffset()};
//        TicketDTO ticketDTO = new TicketDTO(1L, dateTime, null, null, price, "1A",
//                departurePoint, destinationPoint, carrierName);
//        List<TicketDTO> expectedList = Collections.singletonList(ticketDTO);
//
//        when(jdbcTemplate.query(eq(expectedSql), eq(expectedParams), any(TicketRowMapper.class)))
//                .thenReturn(expectedList);
//
//        List<TicketDTO> actualList = ticketRepository.findAll(pageable, dateTime, departurePoint,
//                destinationPoint, carrierName);
//        assertEquals(expectedList, actualList);
//        verify(jdbcTemplate).query(eq(expectedSql), eq(expectedParams), any(TicketRowMapper.class));
//    }
//
//    @Test
//    public void findAllTestWithoutParams() {
//        String expectedSql = "SELECT t.*, r.departure_point, r.destination_point, r.carrier_name" +
//                " FROM tickets t JOIN routes r ON t.route_id = r.id WHERE t.user_id IS NULL";
//        Object[] expectedParams = new Object[0];
//        TicketDTO ticketDTO = new TicketDTO(1L, LocalDateTime.of(2019,02, 22,
//                9, 40, 00), 1L, 1L, BigDecimal.valueOf(10), "1A",
//                "Paris","Vienna", "Java Airlines");
//        List<TicketDTO> expectedList = Collections.singletonList(ticketDTO);
//
//        when(jdbcTemplate.query(eq(expectedSql), eq(expectedParams), any(TicketRowMapper.class)))
//                .thenReturn(expectedList);
//
//        List<TicketDTO> actualList = ticketRepository.findAll();
//        assertEquals(expectedList, actualList);
//        verify(jdbcTemplate).query(eq(expectedSql), eq(expectedParams), any(TicketRowMapper.class));
//    }
}
