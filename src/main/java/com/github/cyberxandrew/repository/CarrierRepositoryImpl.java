package com.github.cyberxandrew.repository;

import com.github.cyberxandrew.exception.carrier.CarrierDeletionException;
import com.github.cyberxandrew.exception.carrier.CarrierSaveException;
import com.github.cyberxandrew.exception.carrier.CarrierUpdateException;
import com.github.cyberxandrew.exception.ticket.TicketNotFoundException;
import com.github.cyberxandrew.exception.ticket.TicketUpdateException;
import com.github.cyberxandrew.mapper.CarrierRowMapper;
import com.github.cyberxandrew.model.Carrier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class CarrierRepositoryImpl implements CarrierRepository {
    private static final Logger logger = LoggerFactory.getLogger(CarrierRepositoryImpl.class);
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private CarrierRowMapper carrierRowMapper;

    @Override
    @Transactional
    public Optional<Carrier> findById(Long carrierId) {
        if (carrierId == null) throw new NullPointerException("Carrier with id = null cannot be found in database");
        String sql = "SELECT * FROM carriers WHERE id = ?";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, new Object[]{carrierId}, carrierRowMapper));
        } catch (EmptyResultDataAccessException ex) {
            logger.warn("Carrier with id {} not found", carrierId);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public List<Carrier> findAll() {
        String sql = "SELECT * FROM carriers";
        return jdbcTemplate.query(sql, carrierRowMapper);
    }

    @Override
    @Transactional
    public Carrier saveCarrier(Carrier carrier) {
        String sql = "INSERT INTO carriers (name, phone_number) VALUES (?, ?)";
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
                preparedStatement.setString(1, carrier.getName());
                preparedStatement.setString(2, carrier.getPhoneNumber());
                return preparedStatement;
            }, keyHolder);

            carrier.setId(keyHolder.getKey().longValue());
            logger.debug("Carrier with id: {} successfully created", carrier.getId());
            return carrier;
        } catch (DataAccessException ex) {
            logger.error("Error while saving carrier: {}", carrier.toString());
            throw new CarrierSaveException("Error while saving carrier", ex);
        }
    }

    @Override
    @Transactional
    public Carrier updateCarrier(Carrier carrier) {
        try {
            String sql = "UPDATE carriers SET name = ?, phone_number = ? WHERE id = ?";
            int updated = jdbcTemplate.update(sql, carrier.getName(), carrier.getPhoneNumber(), carrier.getId());
            if (updated > 0) {
                logger.debug("Updating carrier with id: {} is successful", carrier.getId());
            } else {
                logger.warn("Carrier with id: {} not found for updating", carrier.getId());
                throw new CarrierUpdateException("Carrier not found for updating");
            }
            return carrier;
        } catch (DataAccessException ex) {
            logger.error("Error while updating carrier with id: {}", carrier.getId());
            throw new CarrierUpdateException("Error while updating carrier", ex);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long carrierId) {
        try {
            Optional<Carrier> carrier = findById(carrierId);
            if (carrier.isEmpty()) {
                logger.warn("Carrier with id: {} not found", carrierId);
                throw new TicketNotFoundException("Carrier not found while deletion");
            }
            String sql = "DELETE FROM carriers WHERE id = ?";
            jdbcTemplate.update(sql, carrierId);
            logger.debug("Carrier with id {} successfully deleted", carrierId);
        } catch (DataAccessException ex) {
            logger.error("Error when deleting carrier with id = {}: {}", carrierId, ex.getMessage(), ex);
            throw new CarrierDeletionException("Error when deleting carrier", ex);
        }
    }
}
