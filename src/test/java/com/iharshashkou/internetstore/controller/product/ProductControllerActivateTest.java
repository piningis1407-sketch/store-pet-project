package com.iharshashkou.internetstore.controller.product;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerActivateTest extends BaseProductControllerTest {

    @Test
    @WithMockUser(roles = "ADMIN")
    void activate_shouldReturnNoContent_whenProductExists() throws Exception {
        doNothing().when(productService).activate(1L);

        mockMvc.perform(patch("/api/products/1/activate").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void activate_shouldReturnForbidden_whenUserIsNotAdmin() throws Exception {
        mockMvc.perform(patch("/api/products/1/activate").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void activate_shouldReturnUnauthorized_whenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(patch("/api/products/1/activate"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(productService);
    }
}
