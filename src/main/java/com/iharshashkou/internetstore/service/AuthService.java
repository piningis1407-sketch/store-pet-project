package com.iharshashkou.internetstore.service;

import com.iharshashkou.internetstore.DTO.AuthResponse;
import com.iharshashkou.internetstore.DTO.LoginRequest;
import com.iharshashkou.internetstore.DTO.RegisterRequest;
import com.iharshashkou.internetstore.DTO.Role;
import com.iharshashkou.internetstore.entity.UserEntity;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.iharshashkou.internetstore.repository.UserRepository;
import com.iharshashkou.internetstore.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email уже зарегистрирован"
            );
        }

        UserEntity user = UserEntity.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(
                        request.password()
                ))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        user.getEmail()
                );

        return createAuthResponse(userDetails);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        request.email()
                );

        return createAuthResponse(userDetails);
    }

    private AuthResponse createAuthResponse(UserDetails userDetails) {
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer"
        );
    }

    public AuthResponse refresh(String refreshToken) {
        try {
            if (!jwtService.isRefreshTokenValid(refreshToken)) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid refresh token"
                );
            }

            String email = jwtService.extractUsername(refreshToken);

            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(email);

            return createAuthResponse(userDetails);

        } catch (JwtException | IllegalArgumentException |
                 UsernameNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid or expired refresh token"
            );
        }
    }
}
