package com.github.cyberxandrew.repository;

import com.github.cyberxandrew.dto.TicketWithRouteDataDTO;
import com.github.cyberxandrew.exception.carrier.CarrierDeletionException;
import com.github.cyberxandrew.exception.carrier.CarrierNotFoundException;
import com.github.cyberxandrew.exception.ticket.TicketAvailabilityException;
import com.github.cyberxandrew.exception.ticket.TicketDeletionException;
import com.github.cyberxandrew.exception.ticket.TicketNotFoundException;
import com.github.cyberxandrew.exception.ticket.TicketUpdateException;
import com.github.cyberxandrew.mapper.CarrierRowMapper;
import com.github.cyberxandrew.mapper.TicketDtoRowMapper;
import com.github.cyberxandrew.mapper.TicketRowMapper;
import com.github.cyberxandrew.model.Carrier;
import com.github.cyberxandrew.model.Ticket;
import com.github.cyberxandrew.utils.TicketFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private Long nonExistingId;
    private Long testUserId;
    private Long testTicketId;
    private Ticket testTicket;
    @BeforeEach
    void beforeEach() {
        testTicketId = 1L;
        testUserId = 2L;
        nonExistingId = 999L;

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
        TicketFactory.setTicketWithRouteDataDtoFieldsWithoutUserId(ticketWithRouteDataDTO);
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
        TicketFactory.setTicketWithRouteDataDtoFieldsWithoutUserId(testTicketWithRouteDataDTO);

        List<TicketWithRouteDataDTO> expectedList = new ArrayList<>(Collections.singletonList(testTicketWithRouteDataDTO));

        when(jdbcTemplate.query(eq(expectedSql), eq(expectedParams), any(TicketDtoRowMapper.class)))
                .thenReturn(expectedList);

        List<TicketWithRouteDataDTO> actualList = ticketRepository.findAll(pageable, dateTime, departurePoint,
                destinationPoint, carrierName);

        assertEquals(expectedList, actualList);
        verify(jdbcTemplate).query(eq(expectedSql), eq(expectedParams), any(TicketDtoRowMapper.class));
    }

    @Test
    public void testSaveTicket() {
        Ticket ticketToSave = TicketFactory.createTicketToSave();

        when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenAnswer(invocation -> {
            KeyHolder keyHolder = invocation.getArgument(1);
            keyHolder.getKeyList().add(new java.util.HashMap<String, Object>() {{ put("id", testTicketId); }});
            return 1;});

        Ticket savedTicket = ticketRepository.save(ticketToSave);

        assertEquals(savedTicket, ticketToSave);
        verify(jdbcTemplate, times(1)).update(any(PreparedStatementCreator.class),
                any(KeyHolder.class));
//        verify(logger, times(1)).debug(anyString(), anyLong());
    }

    @Test
    public void testUpdateSuccessful() {
        String sql = "UPDATE tickets SET date_time = ?, user_id = ?, route_id = ?, price = ?, seat_number = ?" +
                " WHERE id = ?";

        Ticket ticketToUpdate = new Ticket();
        TicketFactory.setTicketFieldsWithoutId(ticketToUpdate);
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

        TicketFactory.setTicketFieldsWithoutId(testTicket);

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
        TicketFactory.setTicketFieldsWithoutId(ticket);
        ticket.setId(testTicketId);

        when(jdbcTemplate.update(eq(sql), anyString(), anyLong(), anyLong(),
                any(BigDecimal.class), anyString(), anyLong()))
                .thenThrow(new QueryTimeoutException("Simulated QueryTimeoutException"));

        TicketUpdateException ex = assertThrows(TicketUpdateException.class, () -> ticketRepository.update(ticket));
        assertEquals(ex.getMessage(), "Error while updating ticket");
//        verify(logger, times(1)).error("Error while updating ticket", ex);
    }


    //fixme СЮДА ПЕРЕНЕСТИ ТЕСТЫ УДАЛЕНИЯЯ

    @Test
    public void testIsTicketAvailableTrue() {
        String sql = "SELECT EXISTS (SELECT * FROM tickets WHERE id = ? AND user_id IS NULL)";

        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{testTicketId}), eq(Boolean.class)))
                .thenReturn(true);

        boolean result = ticketRepository.isTicketAvailable(testTicketId);
        assertTrue(result);
//        verify(logger, times(1))
//                .debug("Ticket with id: {} is available: {}", testTicketId, true);
    }

    @Test
    public void testIsTicketAvailableFalse() {
        String sql = "SELECT EXISTS (SELECT * FROM tickets WHERE id = ? AND user_id IS NULL)";

        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{testTicketId}), eq(Boolean.class)))
                .thenReturn(false);

        boolean result = ticketRepository.isTicketAvailable(testTicketId);
        assertFalse(result);
        //        verify(logger, times(1))
//                .debug("Ticket with id: {} is available: {}", testTicketId, false);
    }

    @Test
    public void testIsTicketAvailableDoesNotExists() {
        String sql = "SELECT EXISTS (SELECT * FROM tickets WHERE id = ? AND user_id IS NULL)";

        doThrow(new EmptyResultDataAccessException(1)).when(jdbcTemplate)
                .queryForObject(eq(sql), eq(new Object[]{testTicketId}), eq(Boolean.class));

        assertThrows(TicketAvailabilityException.class, () -> {
            ticketRepository.isTicketAvailable(testTicketId);
//            verify(logger, times(1))
//                .error("Ticket with id: {} availability definition error", testTicketId, true);
        });
    }

    @Test
    public void testDeleteByIdSuccessful() {
        Ticket ticket = new TicketFactory.TicketBuilder()
                .withId(testTicketId)
                .withDateTime(LocalDateTime.now())
                .withUserId(null)
                .withRouteId(2L)
                .withPrice(new BigDecimal("99.99"))
                .withSeatNumber("1A").build();

        String sql1 = "SELECT * FROM tickets WHERE id = ?";
        String sql2 = "DELETE FROM tickets WHERE id = ?";

        when(jdbcTemplate.queryForObject(eq(sql1), eq(new Object[]{testTicketId}), any(TicketRowMapper.class)))
                .thenReturn(ticket);
        when(jdbcTemplate.update(eq(sql2), eq(testTicketId))).thenReturn(1);

        ticketRepository.deleteById(testTicketId);

        verify(jdbcTemplate, times(1)).update(eq(sql2), eq(testTicketId));
//        verify(logger, times(1)).debug(anyString(), anyLong());
    }

    @Test
    public void testDeleteByIdFailed() {
        String sql1 = "SELECT * FROM tickets WHERE id = ?";
        String sql2 = "DELETE FROM tickets WHERE id = ?";

        when(jdbcTemplate.queryForObject(eq(sql1), eq(new Object[]{nonExistingId}), any(TicketRowMapper.class)))
                .thenThrow(EmptyResultDataAccessException.class);

        assertThrows(TicketNotFoundException.class, () -> ticketRepository.deleteById(nonExistingId));
        verify(jdbcTemplate, times(0)).update(eq(sql2), eq(nonExistingId));
//        verify(logger, times(1)).warn(anyString(), anyLong());
    }

    @Test
    public void testDeleteByIdDatabaseError() {
        Ticket ticket = new Ticket();
        String sql1 = "SELECT * FROM tickets WHERE id = ?";
        String sql2 = "DELETE FROM tickets WHERE id = ?";

        when(jdbcTemplate.queryForObject(eq(sql1), eq(new Object[]{testTicketId}), any(TicketRowMapper.class)))
                .thenReturn(ticket);

        when(jdbcTemplate.update(eq(sql2), eq(testTicketId))).thenThrow(DataAccessResourceFailureException.class);

        assertThrows(TicketDeletionException.class, () -> ticketRepository.deleteById(testTicketId));
    }
}
