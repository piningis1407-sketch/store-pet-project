package com.iharshashkou.internetstore.controller.cart;

import com.iharshashkou.internetstore.config.SecurityConfig;
import com.iharshashkou.internetstore.controller.CartController;
import com.iharshashkou.internetstore.repository.UserRepository;
import com.iharshashkou.internetstore.security.CustomUserDetailsService;
import com.iharshashkou.internetstore.security.JwtAuthenticationFilter;
import com.iharshashkou.internetstore.security.JwtService;
import com.iharshashkou.internetstore.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@WebMvcTest(CartController.class)
@AutoConfigureMockMvc
public abstract class BaseCartControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected CartService cartService;

    @MockitoBean
    protected UserRepository userRepository;

    @MockitoBean
    protected JwtService jwtService;

    @MockitoBean
    protected CustomUserDetailsService customUserDetailsService;
}