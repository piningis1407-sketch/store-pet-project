package com.iharshashkou.internetstore.controller;

import com.iharshashkou.internetstore.repository.UserRepository;
import com.iharshashkou.internetstore.security.JwtService;
import com.iharshashkou.internetstore.service.customDesign.CustomDesignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CustomDesignController.class)
@AutoConfigureMockMvc(addFilters = false)
public abstract class BaseCustomDesignControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected CustomDesignService customDesignService;

    @MockitoBean
    protected UserRepository userRepository;

    @MockitoBean
    protected JwtService jwtService;
}
