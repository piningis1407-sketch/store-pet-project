package com.iharshashkou.internetstore.service.auth;

import com.iharshashkou.internetstore.repository.RefreshTokenRepository;
import com.iharshashkou.internetstore.repository.UserRepository;
import com.iharshashkou.internetstore.security.JwtService;
import com.iharshashkou.internetstore.service.AuthService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public abstract class BaseAuthServiceTest {

    @Mock
    protected UserRepository userRepository;

    @Mock
    protected PasswordEncoder passwordEncoder;

    @Mock
    protected AuthenticationManager authenticationManager;

    @Mock
    protected UserDetailsService userDetailsService;

    @Mock
    protected RefreshTokenRepository refreshTokenRepository;

    @Mock
    protected JwtService jwtService;

    @InjectMocks
    protected AuthService authService;
}