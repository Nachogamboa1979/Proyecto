package com.example.microservices.util;
import org.springframework.beans.BeanUtils;
import java.lang.reflect.InvocationTargetException;

public class MapearUtil {
    public static <D, E> E mapDtoToEntity(D dto, E entity) {
        if (dto == null || entity == null) {
            throw new IllegalArgumentException("El DTO y la entidad no pueden ser nulos");
        }
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}
