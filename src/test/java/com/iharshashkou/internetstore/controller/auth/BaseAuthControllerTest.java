package com.iharshashkou.internetstore.controller.auth;

import com.iharshashkou.internetstore.security.JwtService;
import com.iharshashkou.internetstore.controller.AuthController;
import com.iharshashkou.internetstore.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public abstract class BaseAuthControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected AuthService authService;

    @MockitoBean
    protected JwtService jwtService;
}
