package com.iharshashkou.internetstore.controller.product;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerDeactivateTest extends BaseProductControllerTest {

    @Test
    @WithMockUser(roles = "ADMIN")
    void deactivate_shouldReturnNoContent_whenProductExists() throws Exception {
        doNothing().when(productService).deactivate(1L);

        mockMvc.perform(delete("/api/products/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deactivate_shouldReturnForbidden_whenUserIsNotAdmin() throws Exception {
        mockMvc.perform(delete("/api/products/1").with(csrf()))
                .andExpect(status().isForbidden());
    }
}
