package com.github.cyberxandrew.repository;

import com.github.cyberxandrew.dto.TicketWithRouteDataDTO;
import com.github.cyberxandrew.exception.ticket.TicketAvailabilityException;
import com.github.cyberxandrew.exception.ticket.TicketUpdateException;
import com.github.cyberxandrew.mapper.TicketDtoRowMapper;
import com.github.cyberxandrew.mapper.TicketRowMapper;
import com.github.cyberxandrew.model.Ticket;
import com.github.cyberxandrew.utils.ModelGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class TicketRepositoryImplTest {
    @Mock private Logger logger;
    @Mock private JdbcTemplate jdbcTemplate;
    @SuppressWarnings("unused")
    @Mock private TicketRowMapper ticketRowMapper;
    @Mock private TicketDtoRowMapper ticketDtoRowMapper;
    @InjectMocks
    private TicketRepositoryImpl ticketRepository;

    private Long testUserId;
    private Long testTicketId;
    private Ticket testTicket;
    @BeforeEach
    void beforeEach() {
        testTicketId = 1L;
        testUserId = 2L;

        testTicket = new Ticket();
        testTicket.setId(testTicketId);
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

        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{testTicketId}), any(TicketRowMapper.class)))
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

        when(jdbcTemplate.query(eq(sql), eq(new Object[]{testUserId}), any(TicketRowMapper.class)))
                .thenReturn(tickets);

        List<Ticket> result = ticketRepository.findByUserId(testUserId);
        assertFalse(result.isEmpty());
        assertTrue(result.containsAll(Arrays.asList(testTicket, secondTestTicket)));
    }

    @Test
    public void testFindByUserIdFailed() {
        String sql = "SELECT * FROM tickets WHERE user_id = ?";
        testTicket.setUserId(testUserId);

        when(jdbcTemplate.query(eq(sql), eq(new Object[]{testUserId}), any(TicketRowMapper.class)))
                .thenReturn(Collections.emptyList());

        List<Ticket> result = ticketRepository.findByUserId(testUserId);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFindAllWithoutParams() {
        String expectedSql = "SELECT t.*, r.departure_point, r.destination_point, c.name " +
                "FROM tickets t JOIN routes r ON t.route_id = r.id JOIN carriers c ON r.carrier_id = c.id " +
                "WHERE t.user_id IS NULL";
        Object[] expectedParams = new Object[0];

        TicketWithRouteDataDTO ticketWithRouteDataDTO = new TicketWithRouteDataDTO();
        ModelGenerator.setTicketWithRouteDataDtoFieldsWithoutUserId(ticketWithRouteDataDTO);
        List<TicketWithRouteDataDTO> expectedList = new ArrayList<>(Collections.singletonList(ticketWithRouteDataDTO));

        when(jdbcTemplate.query(eq(expectedSql), eq(expectedParams), any(TicketDtoRowMapper.class)))
                .thenReturn(expectedList);

        List<TicketWithRouteDataDTO> actualList = ticketRepository.findAll();

        assertEquals(expectedList, actualList);
        verify(jdbcTemplate, times(1)).query(eq(expectedSql), eq(expectedParams), any(TicketDtoRowMapper.class));
    }

    @Test
    public void testFindAllWithAllParams() {
        String expectedSql = "SELECT t.*, r.departure_point, r.destination_point, c.name " +
                "FROM tickets t JOIN routes r ON t.route_id = r.id JOIN carriers c ON r.carrier_id = c.id " +
                "WHERE t.user_id IS NULL AND t.date_time LIKE ? AND r.departure_point LIKE ? " +
                "AND r.destination_point LIKE ? AND c.name LIKE ? LIMIT ? OFFSET ?";

        Pageable pageable = PageRequest.of(0, 2);
        LocalDateTime dateTime = LocalDateTime.now();
        String departurePoint = "Saints-Petersburg";
        String destinationPoint = "Moscow";
        String carrierName = "Java Airlines";

        Object[] expectedParams = new Object[]{"%" + dateTime + "%", "%" + departurePoint + "%",
                "%" + destinationPoint + "%", "%" + carrierName + "%", pageable.getPageSize(), pageable.getOffset()};

        TicketWithRouteDataDTO testTicketWithRouteDataDTO = new TicketWithRouteDataDTO();
        ModelGenerator.setTicketWithRouteDataDtoFieldsWithoutUserId(testTicketWithRouteDataDTO);

        List<TicketWithRouteDataDTO> expectedList = new ArrayList<>(Collections.singletonList(testTicketWithRouteDataDTO));

        when(jdbcTemplate.query(eq(expectedSql), eq(expectedParams), any(TicketDtoRowMapper.class)))
                .thenReturn(expectedList);

        List<TicketWithRouteDataDTO> actualList = ticketRepository.findAll(pageable, dateTime, departurePoint,
                destinationPoint, carrierName);

        assertEquals(expectedList, actualList);
        verify(jdbcTemplate).query(eq(expectedSql), eq(expectedParams), any(TicketDtoRowMapper.class));
    }

    @Test
    public void testUpdateSuccessful() {
        String sql = "UPDATE tickets SET date_time = ?, user_id = ?, route_id = ?, price = ?, seat_number = ?" +
                " WHERE id = ?";

        Ticket ticketToUpdate = new Ticket();
        ModelGenerator.setTicketFieldsWithoutId(ticketToUpdate);
        ticketToUpdate.setId(testTicketId);

//        when(logger.isDebugEnabled()).thenReturn(true);
//        doNothing().when(logger).debug(anyString(), any(Object.class));//

        when(jdbcTemplate.update(eq(sql), anyString(), anyLong(), anyLong(),
                any(BigDecimal.class), anyString(), anyLong())).thenReturn(1);

        Ticket updatedTicket = ticketRepository.update(ticketToUpdate);

        assertEquals(ticketToUpdate, updatedTicket); //?
//        verify(logger, times(1))
//                .debug("Updating ticket with id: {} is successful", testTicket.getId());
    }

    @Test
    public void testUpdateTicketNotFound() {
        String sql = "UPDATE tickets SET date_time = ?, user_id = ?, route_id = ?, price = ?, seat_number = ?" +
                " WHERE id = ?";

        ModelGenerator.setTicketFieldsWithoutId(testTicket);

        when(jdbcTemplate.update(eq(sql), anyString(), anyLong(), anyLong(),
                any(BigDecimal.class), anyString(), anyLong())).thenReturn(0);

        assertThrows(TicketUpdateException.class, () -> ticketRepository.update(testTicket));
//        verify(logger, times(1))
//                .warn("Ticket with id: {} not found for updating", testTicket.getId());
    }

    @Test
    public void testUpdateDataBaseException() {
        String sql = "UPDATE tickets SET date_time = ?, user_id = ?, route_id = ?, price = ?, seat_number = ?" +
                " WHERE id = ?";

        Ticket ticket = new Ticket();
        ModelGenerator.setTicketFieldsWithoutId(ticket);
        ticket.setId(testTicketId);

        when(jdbcTemplate.update(eq(sql), anyString(), anyLong(), anyLong(),
                any(BigDecimal.class), anyString(), anyLong()))
                .thenThrow(new QueryTimeoutException("Simulated QueryTimeoutException"));

        TicketUpdateException ex = assertThrows(TicketUpdateException.class, () -> ticketRepository.update(ticket));
        assertEquals(ex.getMessage(), "Error while updating ticket");
//        verify(logger, times(1)).error("Error while updating ticket", ex);
    }

    @Test
    public void testIsTicketAvailableTrue() {
        String sql = "SELECT EXISTS (SELECT 1 FROM tickets WHERE id = ? AND user_id IS NULL)";

        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{testTicketId}), eq(Boolean.class)))
                .thenReturn(true);

        boolean result = ticketRepository.isTicketAvailable(testTicketId);
        assertTrue(result);
//        verify(logger, times(1))
//                .debug("Ticket with id: {} is available: {}", testTicketId, true);
    }

    @Test
    public void testIsTicketAvailableFalse() {
        String sql = "SELECT EXISTS (SELECT 1 FROM tickets WHERE id = ? AND user_id IS NULL)";

        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{testTicketId}), eq(Boolean.class)))
                .thenReturn(false);

        boolean result = ticketRepository.isTicketAvailable(testTicketId);
        assertFalse(result);
        //        verify(logger, times(1))
//                .debug("Ticket with id: {} is available: {}", testTicketId, false);
    }

    @Test
    public void testIsTicketAvailableDoesNotExists() {
        String sql = "SELECT EXISTS (SELECT 1 FROM tickets WHERE id = ? AND user_id IS NULL)";

        doThrow(new EmptyResultDataAccessException(1)).when(jdbcTemplate)
                .queryForObject(eq(sql), eq(new Object[]{testTicketId}), eq(Boolean.class));

        assertThrows(TicketAvailabilityException.class, () -> {
            ticketRepository.isTicketAvailable(testTicketId);
//            verify(logger, times(1))
//                .error("Ticket with id: {} availability definition error", testTicketId, true);
        });
    }
}
