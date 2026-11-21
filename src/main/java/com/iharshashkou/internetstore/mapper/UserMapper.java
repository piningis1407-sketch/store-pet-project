package com.iharshashkou.internetstore.mapper;

import com.iharshashkou.internetstore.DTO.user.UserRequest;
import com.iharshashkou.internetstore.DTO.user.UserResponse;
import com.iharshashkou.internetstore.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(UserRequest request) {
        return User.builder()
                .name(request.name())
                .email(request.email())
                .password(request.password())
                .build();
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
