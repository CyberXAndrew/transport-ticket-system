package com.github.cyberxandrew.repository;

import com.github.cyberxandrew.exception.route.RouteDeletionException;
import com.github.cyberxandrew.exception.route.RouteNotFoundException;
import com.github.cyberxandrew.exception.route.RouteSaveException;
import com.github.cyberxandrew.exception.route.RouteUpdateException;
import com.github.cyberxandrew.model.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class RouteRepositoryImpl implements RouteRepository {
    private static final Logger logger = LoggerFactory.getLogger(RouteRepositoryImpl.class);
    @Autowired private JdbcTemplate jdbcTemplate;
    private RowMapper<Route> routeRowMapper = new BeanPropertyRowMapper<>(Route.class);

    @Override
    @Transactional
    public Optional<Route> findById(Long routeId) {
        if (routeId == null) throw new NullPointerException("Route with id = null cannot be found in database");
        String sql = "SELECT * FROM routes WHERE id = ?";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, new Object[]{routeId}, routeRowMapper));
        } catch (EmptyResultDataAccessException ex) {
            logger.warn("Route with id {} not found", routeId);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public List<Route> findAll() {
        String sql = "SELECT * FROM routes";
        return jdbcTemplate.query(sql, routeRowMapper);
    }

    @Override
    @Transactional
    public Route save(Route route) {
        String sql = "INSERT INTO routes (departure_point, destination_point, carrier_id, duration)" +
                " VALUES (?, ?, ?, ?)";
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
                preparedStatement.setString(1, route.getDeparturePoint());
                preparedStatement.setString(2, route.getDestinationPoint());
                preparedStatement.setLong(3, route.getCarrierId());
                preparedStatement.setInt(4, route.getDuration());
                return preparedStatement;
            }, keyHolder);

            route.setId(keyHolder.getKey().longValue());
            logger.debug("Route with id: {} successfully created", route.getId());
            return route;
        } catch (DataAccessException ex) {
            logger.error("Error while saving route: {}", route.toString());
            throw new RouteSaveException("Error while saving route", ex);
        }
    }

    @Override
    @Transactional
    public Route update(Route route) {
        try {
            String sql = "UPDATE routes SET departure_point = ?, destination_point = ?, carrier_id = ?, duration = ?" +
                    " WHERE id = ?";
            int updated = jdbcTemplate.update(sql, route.getDeparturePoint(), route.getDestinationPoint(),
                    route.getCarrierId(), route.getDuration(), route.getId());
            if (updated > 0) {
                logger.debug("Updating route with id: {} is successful", route.getId());
            } else {
                logger.warn("Route with id: {} not found for updating", route.getId());
                throw new RouteUpdateException("Route not found for updating");
            }
            return route;
        } catch (DataAccessException ex) {
            logger.error("Error while updating route with id: {}", route.getId());
            throw new RouteUpdateException("Error while updating route", ex);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long routeId) {
        try {
            Optional<Route> route = findById(routeId);
            if (route.isEmpty()) {
                logger.warn("Route with id: {} not found", routeId);
                throw new RouteNotFoundException("Route not found while deletion");
            }
            String sql = "DELETE FROM routes WHERE id = ?";
            jdbcTemplate.update(sql, routeId);
            logger.debug("Route with id {} successfully deleted", routeId);
        } catch (DataAccessException ex) {
            logger.error("Error when deleting route with id = {}: {}", routeId, ex.getMessage(), ex);
            throw new RouteDeletionException("Error when deleting route", ex);
        }
    }
}
