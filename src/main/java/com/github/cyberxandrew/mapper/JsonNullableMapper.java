package com.github.cyberxandrew.mapper;

import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.openapitools.jackson.nullable.JsonNullable;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
//@Component
public abstract class JsonNullableMapper {

    public <E> JsonNullable<E> wrap(E entity) {
        return JsonNullable.of(entity);
    }

    public <E> E unwrap(JsonNullable<E> jsonNullable) {
        return jsonNullable == null ? null : jsonNullable.orElse(null);
    }

    @Condition
    public <E> boolean isPresent(JsonNullable<E> nullable) {
        return nullable != null && nullable.isPresent();
    }
}
