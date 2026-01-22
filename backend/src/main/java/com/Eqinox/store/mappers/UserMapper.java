package com.Eqinox.store.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.Eqinox.store.dtos.UserDto;
import com.Eqinox.store.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    //@Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    UserDto toDto(User user);
}
