package com.github.cyberxandrew.repository;

import com.github.cyberxandrew.exception.carrier.CarrierUpdateException;
import com.github.cyberxandrew.mapper.CarrierRowMapper;
import com.github.cyberxandrew.model.Carrier;
import com.github.cyberxandrew.utils.CarrierFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.context.ActiveProfiles;

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
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
//@ExtendWith(MockitoExtension.class) //fixme ADD EXTENDS WITH ?
class CarrierRepositoryImplTest {

    @Mock private Logger logger;
    @Mock private JdbcTemplate jdbcTemplate;
    @Mock private CarrierRowMapper carrierRowMapper;
    @InjectMocks private CarrierRepositoryImpl carrierRepository;

    private Long carrierId1;
    private Long carrierId2;
    private Carrier testCarrier;

    @BeforeEach
    void beforeEach() {
        carrierId1 = 1L;
        carrierId2 = 2L;

        testCarrier = new Carrier();
        testCarrier.setId(carrierId1);
    }

    @Test
    public void testFindByIdSuccessful() {
        String sql = "SELECT * FROM carriers WHERE id = ?";

        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{carrierId1}), any(CarrierRowMapper.class)))
                .thenReturn(testCarrier);

        Optional<Carrier> actual = carrierRepository.findById(carrierId1);

        assertTrue(actual.isPresent());
        assertEquals(actual.get(), testCarrier);
    }

    @Test
    public void testFindByIdFailed() {
        String sql = "SELECT * FROM carriers WHERE id = ?";

        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{carrierId1}), any(CarrierRowMapper.class)))
                .thenThrow(new EmptyResultDataAccessException(1));

        Optional<Carrier> actual = carrierRepository.findById(carrierId1);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testFindAllSuccessful() {
        String sql = "SELECT * FROM carriers";

        List<Carrier> carriers = new ArrayList<>();
        Carrier carrier1 = CarrierFactory.createCarrierToSave();
        carrier1.setId(carrierId1);
        Carrier carrier2 = CarrierFactory.createCarrierToSave();
        carrier2.setId(carrierId2);
        Collections.addAll(carriers, carrier1, carrier2);

        when(jdbcTemplate.query(eq(sql), any(CarrierRowMapper.class)))
                .thenReturn(carriers);

        List<Carrier> allCarriers = carrierRepository.findAll();
        assertFalse(allCarriers.isEmpty());
        assertEquals(allCarriers.size(), 2);
        assertTrue(allCarriers.containsAll(Arrays.asList(carrier1, carrier2)));
    }

    @Test
    public void testSaveCarrier() {
        Carrier carrierToSave = CarrierFactory.createCarrierToSave();

        when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenAnswer(invocation -> {
            KeyHolder keyHolder = invocation.getArgument(1);
            keyHolder.getKeyList().add(new java.util.HashMap<String, Object>() {{ put("id", carrierId1); }});
            return 1;});

        Carrier savedCarrier = carrierRepository.saveCarrier(carrierToSave);

        assertEquals(savedCarrier, carrierToSave);
        verify(jdbcTemplate, times(1)).update(any(PreparedStatementCreator.class),
                any(KeyHolder.class));
//        verify(logger, times(1)).debug(anyString(), anyLong());
    }

    @Test
    public void testUpdateCarrierSuccessful() {
        String sql = "UPDATE carriers SET name = ?, phone_number = ? WHERE id = ?";

        Carrier carrierToUpdate = new Carrier();
        carrierToUpdate.setId(carrierId1);
        carrierToUpdate.setName("Some name");
        carrierToUpdate.setPhoneNumber("123456789");

        when(jdbcTemplate.update(eq(sql), anyString(), anyString(), anyLong())).thenReturn(1);

        Carrier updatedCarrier = carrierRepository.updateCarrier(carrierToUpdate);

        assertEquals(carrierToUpdate, updatedCarrier);
//        verify(logger, times(1)).debug(anyString(), anyLong());
    }

    @Test
    public void testUpdateCarrierFailed() {
        String sql = "UPDATE carriers SET name = ?, phone_number = ? WHERE id = ?";

        Carrier carrierToUpdate = new Carrier();
        carrierToUpdate.setId(carrierId1);
        carrierToUpdate.setName("Some name");
        carrierToUpdate.setPhoneNumber("123456789");

        when(jdbcTemplate.update(eq(sql), anyString(), anyString(), anyLong())).thenReturn(0);

        assertThrows(CarrierUpdateException.class, () -> carrierRepository.updateCarrier(carrierToUpdate));
//        verify(logger, times(1)).warn(anyString(), anyLong());
    }
}