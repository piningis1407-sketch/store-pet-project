package com.iharshashkou.internetstore.service.user;

import com.iharshashkou.internetstore.mapper.UserMapper;
import com.iharshashkou.internetstore.repository.UserRepository;
import com.iharshashkou.internetstore.service.UserService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public abstract class BaseUserServiceTest {

    @Mock
    protected UserRepository userRepository;

    @Mock
    protected UserMapper userMapper;

    @Mock
    protected PasswordEncoder passwordEncoder;

    @InjectMocks
    protected UserService userService;
}
