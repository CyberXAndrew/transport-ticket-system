package com.github.cyberxandrew.mapper;

import com.github.cyberxandrew.model.Route;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class RouteRowMapper {
    public RowMapper<Route> rowMap() {
        return new BeanPropertyRowMapper<>(Route.class);
    }
}