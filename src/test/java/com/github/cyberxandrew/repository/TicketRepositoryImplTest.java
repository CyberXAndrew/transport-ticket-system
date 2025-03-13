package com.github.cyberxandrew.repository;

import com.github.cyberxandrew.exception.ticket.TicketSaveException;
import com.github.cyberxandrew.model.Ticket;
import com.github.cyberxandrew.mapper.TicketRowMapper;
import com.github.cyberxandrew.exception.ticket.TicketAvailabilityException;
import com.github.cyberxandrew.exception.ticket.TicketDeleteException;
import com.github.cyberxandrew.exception.ticket.TicketNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Collections;

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

        jdbcTemplate.execute("DROP TABLE ID EXISTS tickets");
        jdbcTemplate.execute("CREATE TABLE tickets (" +
                "id BIGINT AUTO_INCREMENT PRIMARY_KEY," +
                "date_time TIMESTAMP," +
                "user_id BIGINT," +
                "route_id BIGINT," +
                "price DECIMAL (10,2)," +
                "seat_number VARCHAR(255))");
    }

    @Test
    public void testFindByIdSuccessful() {
        String sql = "SELECT * FROM tickets WHERE id = ?";

        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{testTicketId}), any(TicketRowMapper.class)))
                .thenReturn(testTicket);

        Optional<Ticket> actual = ticketRepository.findById(testTicketId);
        assertTrue(actual.isPresent());
        assertEquals(actual.get(), testTicket);
    }
    @Test
    public void testFindByIdFailed() {
        String sql = "SELECT * FROM tickets WHERE id = ?";

        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{testTicketId}), ticketRowMapper))
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

        when(jdbcTemplate.query(eq(sql), eq(new Object[]{testUserId}), ticketRowMapper))
                .thenReturn(tickets);

        List<Ticket> result = ticketRepository.findByUserId(testUserId);
        assertFalse(result.isEmpty());
        assertTrue(result.containsAll(Arrays.asList(testTicket, secondTestTicket)));
    }
    @Test
    public void testFindByUserIdFailed() {
        String sql = "SELECT * FROM tickets WHERE user_id = ?";
        testTicket.setUserId(testUserId);

        when(jdbcTemplate.query(eq(sql), eq(new Object[]{testUserId}), ticketRowMapper))
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

        testTicket.setId(testTicketId);
        setTicketFieldsWithoutId(testTicket);

        when(logger.isDebugEnabled()).thenReturn(true);
        when(jdbcTemplate.update(eq(sql), any(LocalDateTime.class), anyLong(), anyLong(),
                any(BigInteger.class), anyString(), anyLong())).thenReturn(1);

        Ticket updatedTicket = ticketRepository.save(testTicket);

        assertEquals(testTicket, updatedTicket);
        verify(logger, times(1))
                .debug("Updating ticket with id: {} is successful", testTicket.getId());
    }

    @Test
    public void testSaveTicketNotFoundUpdate() {
        String sql = "UPDATE tickets SET date_time = ?, user_id = ?, route_id = ?, price = ?, seat_number = ?" +
                " WHERE id = ?";

        testTicket.setId(testTicketId);
        setTicketFieldsWithoutId(testTicket);

        when(jdbcTemplate.update(eq(sql), any(LocalDateTime.class), anyLong(), anyLong(),
                any(BigInteger.class), anyString(), anyLong())).thenReturn(0);

        TicketSaveException ex = assertThrows(TicketSaveException.class, () -> {
            ticketRepository.save(testTicket);
        });
        verify(logger, times(1))
                .warn("Ticket with id: {} not found for updating", testTicket.getId());
    }

    @Test
    public void testSaveDataBaseException() {
        String sql = "UPDATE tickets SET date_time = ?, user_id = ?, route_id = ?, price = ?, seat_number = ?" +
                " WHERE id = ?";

        testTicket.setId(testTicketId);
        setTicketFieldsWithoutId(testTicket);

        when(jdbcTemplate.update(eq(sql), any(LocalDateTime.class), anyLong(), anyLong(),
                any(BigInteger.class), anyString(), anyLong())).thenThrow(DataAccessException.class);

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            ticketRepository.save(testTicket);
        });
        verify(logger, times(1)).error("Error while saving/updating ticket", ex);
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

    private void setTicketFieldsWithoutId(Ticket ticket) {
        ticket.setDateTime(LocalDateTime.now());
        ticket.setUserId(2L);
        ticket.setRouteId(3L);
        ticket.setPrice(new BigDecimal("15.5"));
        ticket.setSeatNumber("1A");
    }
}
