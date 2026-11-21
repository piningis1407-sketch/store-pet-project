package com.iharshashkou.internetstore.controller.product;

import com.iharshashkou.internetstore.config.SecurityConfig;
import com.iharshashkou.internetstore.controller.ProductController;
import com.iharshashkou.internetstore.security.CustomUserDetailsService;
import com.iharshashkou.internetstore.security.JwtAuthenticationFilter;
import com.iharshashkou.internetstore.security.JwtService;
import com.iharshashkou.internetstore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc
public abstract class BaseProductControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected ProductService productService;

    @MockitoBean
    protected JwtService jwtService;

    @MockitoBean
    protected CustomUserDetailsService customUserDetailsService;
}
