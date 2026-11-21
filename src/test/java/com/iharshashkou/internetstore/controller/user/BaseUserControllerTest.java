package com.iharshashkou.internetstore.controller.user;

import com.iharshashkou.internetstore.config.SecurityConfig;


import com.iharshashkou.internetstore.controller.UserController;
import com.iharshashkou.internetstore.security.CustomUserDetailsService;
import com.iharshashkou.internetstore.security.JwtAuthenticationFilter;
import com.iharshashkou.internetstore.security.JwtService;
import com.iharshashkou.internetstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public abstract class BaseUserControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected UserService userService;

    @MockitoBean
    protected JwtService jwtService;

    @MockitoBean
    protected CustomUserDetailsService customUserDetailsService;
}
