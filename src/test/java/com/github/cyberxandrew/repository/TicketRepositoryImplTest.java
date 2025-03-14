package com.github.cyberxandrew.repository;

import com.github.cyberxandrew.model.Ticket;
import com.github.cyberxandrew.dto.TicketDTO;
import com.github.cyberxandrew.mapper.TicketRowMapper;
import com.github.cyberxandrew.mapper.TicketDtoRowMapper;
import com.github.cyberxandrew.exception.ticket.TicketSaveException;
import com.github.cyberxandrew.exception.ticket.TicketAvailabilityException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

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
    public void testSaveSuccessfulUpdate() {
        String sql = "UPDATE tickets SET date_time = ?, user_id = ?, route_id = ?, price = ?, seat_number = ?" +
                " WHERE id = ?";

        testTicket.setId(testTicketId);
        setTicketFieldsWithoutId(testTicket);

        when(logger.isDebugEnabled()).thenReturn(true);
        doNothing().when(logger).debug(anyString(), any(Object.class));//

        when(jdbcTemplate.update(eq(sql), any(LocalDateTime.class), anyLong(), anyLong(),
                any(BigDecimal.class), anyString(), anyLong())).thenReturn(1);

        Ticket updatedTicket = ticketRepository.save(testTicket);

        assertEquals(testTicket, updatedTicket); //?
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

    @Test
    public void testIsTicketAvailableTrue() {
        String sql = "SELECT EXISTS (SELECT 1 FROM tickets WHERE id = ? AND user_id IS NULL)";

        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{testTicketId}), eq(Boolean.class)))
                .thenReturn(true);

        boolean result = ticketRepository.isTicketAvailable(testTicketId);
        assertTrue(result);
        verify(logger, times(1))
                .debug("Ticket with id: {} is available: {}", testTicketId, result);
    }

    @Test
    public void testIsTicketAvailableFalse() {
        String sql = "SELECT EXISTS (SELECT 1 FROM tickets WHERE id = ? AND user_id IS NULL)";

        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{testTicketId}), eq(Boolean.class)))
                .thenReturn(false);

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
    public void testFindAllWithAllParams() {
        String expectedSql = "SELECT t.*, r.departure_point, r.destination_point, r.carrier_name" +
                " FROM tickets t JOIN routes r ON t.route_id = r.id WHERE t.user_id IS NULL" +
                " AND date_time = ? AND departure_point LIKE ? AND destination_point LIKE ? " +
                "AND carrier_name LIKE ? LIMIT ? OFFSET ?";

        Pageable pageable = PageRequest.of(0, 1);
        LocalDateTime dateTime = LocalDateTime.now();
        String departurePoint = "Saints-Petersburg";
        String destinationPoint = "Moscow";
        String carrierName = "Java Airlines";

        Object[] expectedParams = new Object[]{dateTime, "%" + departurePoint + "%", "%" + destinationPoint + "%",
                "%" + carrierName + "%", pageable.getPageSize(), pageable.getOffset()};

        TicketDTO testTicketDTO = new TicketDTO();
        setTicketDtoFieldsWithoutId(testTicketDTO);
        testTicketDTO.setId(testTicketId);

        List<TicketDTO> expectedList = new ArrayList<>(Collections.singletonList(testTicketDTO));

        when(jdbcTemplate.query(eq(expectedSql), eq(expectedParams), any(TicketDtoRowMapper.class)))
                .thenReturn(expectedList);

        List<TicketDTO> actualList = ticketRepository.findAll(pageable, dateTime, departurePoint,
                destinationPoint, carrierName);

        assertEquals(expectedList, actualList);
        verify(jdbcTemplate).query(eq(expectedSql), eq(expectedParams), any(TicketDtoRowMapper.class));
    }
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
        ticket.setPrice(new BigDecimal("123.45"));
        ticket.setSeatNumber("1A");
    }

    private void setTicketDtoFieldsWithoutId(TicketDTO ticketDto) {
        ticketDto.setDateTime(LocalDateTime.now());
        ticketDto.setUserId(null);
        ticketDto.setRouteId(3L);
        ticketDto.setPrice(new BigDecimal("123.45"));
        ticketDto.setSeatNumber("1A");

        ticketDto.setDeparturePoint("Saints-Petersburg");
        ticketDto.setDestinationPoint("Moscow");
        ticketDto.setCarrierName("Java Airlines");
    }
}
