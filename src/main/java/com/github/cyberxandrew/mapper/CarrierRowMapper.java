package com.github.cyberxandrew.mapper;

import com.github.cyberxandrew.exception.carrier.CarrierMappingException;
import com.github.cyberxandrew.model.Carrier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CarrierRowMapper implements RowMapper<Carrier> {
    private static final Logger logger = LoggerFactory.getLogger(CarrierRowMapper.class);
    @Override
    public Carrier mapRow(ResultSet rs, int rowNum) throws SQLException {
        Carrier carrier = new Carrier();
        long id = -1;
        try {
            id = rs.getLong("id");
            if (rs.wasNull()) {
                logger.error("Ошибка при извлечении перевозчика: id is NULL");
                throw new CarrierMappingException("Error while mapping carrier: id is NULL");
            }

            carrier.setId(id);
            carrier.setName(rs.getString("name"));
            carrier.setPhoneNumber(rs.getString("phone_number"));

        } catch (SQLException ex) {
            logger.error("Ошибка при извлечении перевозчика с id: {}", id, ex);
            throw new CarrierMappingException("Error while mapping carrier with id: " + id, ex);
        }

        return carrier;
    }
}
