package mapper;

import DTO.UserRequest;
import DTO.UserResponse;
import entity.UserEntity;

public class UserMapper {
    public UserEntity toEntity(UserRequest request) {
        return UserEntity.builder()
                .name(request.name())
                .email(request.email())
                .password(request.password())
                .role(request.role())
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
