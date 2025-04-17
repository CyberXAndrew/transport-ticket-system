package com.github.cyberxandrew.repository;

import com.github.cyberxandrew.exception.route.RouteDeletionException;
import com.github.cyberxandrew.exception.route.RouteNotFoundException;
import com.github.cyberxandrew.exception.route.RouteUpdateException;
import com.github.cyberxandrew.model.Route;
import com.github.cyberxandrew.utils.RouteFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class RouteRepositoryImplTest {
    @Mock private JdbcTemplate jdbcTemplate;
    @InjectMocks private RouteRepositoryImpl routeRepository;

    private Long nonExistingId;
    private Long routeId1;
    private Long routeId2;
    private String departurePoint;
    private String destinationPoint;
    private Long carrierId;
    private Integer duration;
    private Route testRoute;

    @BeforeEach
    void beforeEach() {
        nonExistingId = 999L;
        routeId1 = 1L;
        routeId2 = 2L;
        departurePoint = "Paris";
        destinationPoint = "Vienna";
        carrierId = 3L;
        duration = 60;

        testRoute = new Route();
        testRoute.setId(routeId1);
    }

    @Test
    public void testFindByIdSuccessful() {
        String sql = "SELECT * FROM routes WHERE id = ?";

        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{routeId1}), any(RowMapper.class)))
                .thenReturn(testRoute);

        Optional<Route> actual = routeRepository.findById(routeId1);

        assertTrue(actual.isPresent());
        assertEquals(actual.get(), testRoute);
    }

    @Test
    public void testFindByIdFailed() {
        String sql = "SELECT * FROM routes WHERE id = ?";

        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{routeId1}), any(RowMapper.class)))
                .thenThrow(new EmptyResultDataAccessException(1));

        assertThrows(RouteNotFoundException.class, () -> routeRepository.findById(routeId1));
    }

    @Test
    public void testFindAllSuccessful() {
        String sql = "SELECT * FROM routes";

        List<Route> routes = new ArrayList<>();
        Route route1 = RouteFactory.createRouteToSave();
        route1.setId(routeId1);
        Route route2 = RouteFactory.createRouteToSave();
        route2.setId(routeId2);
        Collections.addAll(routes, route1, route2);

        when(jdbcTemplate.query(eq(sql), any(RowMapper.class)))
                .thenReturn(routes);

        List<Route> allRoutes = routeRepository.findAll();
        assertFalse(allRoutes.isEmpty());
        assertEquals(allRoutes.size(), 2);
        assertTrue(allRoutes.containsAll(Arrays.asList(route1, route2)));
    }

    @Test
    public void testSaveRoute() {
        Route routeToSave = RouteFactory.createRouteToSave();

        when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenAnswer(invocation -> {
            KeyHolder keyHolder = invocation.getArgument(1);
            keyHolder.getKeyList().add(new java.util.HashMap<String, Object>() {{ put("id", routeId1); }});
            return 1;});

        Route savedRoute = routeRepository.save(routeToSave);

        assertEquals(savedRoute, routeToSave);
        verify(jdbcTemplate, times(1)).update(any(PreparedStatementCreator.class),
                any(KeyHolder.class));
    }

    @Test
    public void testUpdateRouteSuccessful() {
        String sql = "UPDATE routes SET departure_point = ?, destination_point = ?, carrier_id = ?, duration = ?" +
                " WHERE id = ?";

        Route routeToUpdate = new Route();
        routeToUpdate.setId(routeId1);
        routeToUpdate.setDeparturePoint(departurePoint);
        routeToUpdate.setDestinationPoint(destinationPoint);
        routeToUpdate.setCarrierId(carrierId);
        routeToUpdate.setDuration(duration);

        when(jdbcTemplate.update(eq(sql), anyString(), anyString(), anyLong(), anyInt(), anyLong())).thenReturn(1);

        Route updatedRoute = routeRepository.update(routeToUpdate);

        assertEquals(routeToUpdate, updatedRoute);
    }

    @Test
    public void testUpdateRouteFailed() {
        String sql = "UPDATE routes SET departure_point = ?, destination_point = ?, carrier_id = ?, duration = ?" +
                " WHERE id = ?";

        Route routeToUpdate = new Route();
        routeToUpdate.setId(routeId1);
        routeToUpdate.setDeparturePoint(departurePoint);
        routeToUpdate.setDestinationPoint(destinationPoint);
        routeToUpdate.setCarrierId(carrierId);
        routeToUpdate.setDuration(duration);

        when(jdbcTemplate.update(eq(sql), anyString(), anyString(), anyLong())).thenReturn(0);

        assertThrows(RouteUpdateException.class, () -> routeRepository.update(routeToUpdate));
    }

    @Test
    public void testDeleteByIdSuccessful() {
        Route route = new RouteFactory.RouteBuilder()
                .withRouteId(routeId1)
                .withRouteDeparturePoint(departurePoint)
                .withRouteDestinationPoint(destinationPoint)
                .withRouteCarrierId(carrierId)
                .withRouteDuration(duration)
                .build();
        String sql1 = "SELECT * FROM routes WHERE id = ?";
        String sql2 = "DELETE FROM routes WHERE id = ?";
        String sql3 = "SELECT COUNT(*) FROM routes WHERE route_id = ?";

        when(jdbcTemplate.queryForObject(sql3, Integer.class, routeId1)).thenReturn(0);
        when(jdbcTemplate.queryForObject(eq(sql1), eq(new Object[]{routeId1}), any(RowMapper.class)))
                .thenReturn(route);
        when(jdbcTemplate.update(eq(sql2), eq(routeId1))).thenReturn(1);

        routeRepository.deleteById(routeId1);

        verify(jdbcTemplate, times(1)).update(eq(sql2), eq(routeId1));
    }

    @Test
    public void testDeleteByIdFailed() {
        String sql1 = "SELECT * FROM routes WHERE id = ?";
        String sql2 = "DELETE FROM routes WHERE id = ?";
        String sql3 = "SELECT COUNT(*) FROM routes WHERE route_id = ?";

        when(jdbcTemplate.queryForObject(sql3, Integer.class, routeId1)).thenReturn(0);
        when(jdbcTemplate.queryForObject(eq(sql1), eq(new Object[]{nonExistingId}), any(RowMapper.class)))
                .thenThrow(EmptyResultDataAccessException.class);

        assertThrows(RouteNotFoundException.class, () -> routeRepository.deleteById(nonExistingId));
        verify(jdbcTemplate, times(0)).update(eq(sql2), eq(nonExistingId));
    }

    @Test
    public void testDeleteByIdDatabaseError() {
        Route route = new Route();
        String sql1 = "SELECT * FROM routes WHERE id = ?";
        String sql2 = "DELETE FROM routes WHERE id = ?";
        String sql3 = "SELECT COUNT(*) FROM routes WHERE route_id = ?";

        when(jdbcTemplate.queryForObject(sql3, Integer.class, routeId1)).thenReturn(0);
        when(jdbcTemplate.queryForObject(eq(sql1), eq(new Object[]{routeId1}), any(RowMapper.class)))
                .thenReturn(route);
        when(jdbcTemplate.update(eq(sql2), eq(routeId1))).thenThrow(DataAccessResourceFailureException.class);

        assertThrows(RouteDeletionException.class, () -> routeRepository.deleteById(routeId1));
    }
}