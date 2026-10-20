package com.iharshashkou.internetstore.mapper;

import com.iharshashkou.internetstore.DTO.UserRequest;
import com.iharshashkou.internetstore.DTO.UserResponse;
import com.iharshashkou.internetstore.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserEntity toEntity(UserRequest request) {
        return UserEntity.builder()
                .name(request.name())
                .email(request.email())
                .password(request.password())
                .build();
    }

    public UserResponse toResponse(UserEntity user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
