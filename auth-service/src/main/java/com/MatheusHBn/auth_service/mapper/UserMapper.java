package com.MatheusHBn.auth_service.mapper;

import com.MatheusHBn.auth_service.domain.User;
import com.MatheusHBn.auth_service.dto.AuthResponse;
import com.MatheusHBn.auth_service.dto.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(RegisterRequest request);

    AuthResponse toAuthResponse(User user);
}

