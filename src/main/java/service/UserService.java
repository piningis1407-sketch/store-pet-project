package service;

import DTO.UserRequest;
import DTO.UserResponse;
import entity.UserEntity;
import exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "Пользователь с таким email уже существует"
            );
        }

        UserEntity user = userMapper.toEntity(request);

        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        UserEntity savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return userMapper.toResponse(findUser(id));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse update(Long id, UserRequest request) {
        UserEntity user = findUser(id);

        userRepository.findByEmail(request.email())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Этот email уже используется"
                    );
                });

        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(request.role());
        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        UserEntity updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    public void delete(Long id) {
        UserEntity user = findUser(id);
        userRepository.delete(user);
    }

    private UserEntity findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}