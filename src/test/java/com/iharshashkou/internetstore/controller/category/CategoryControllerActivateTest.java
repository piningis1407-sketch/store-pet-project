package com.iharshashkou.internetstore.controller.category;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryControllerActivateTest extends BaseCategoryControllerTest {

    @Test
    @WithMockUser(roles = "ADMIN")
    void activate_shouldReturnNoContent_whenUserIsAdmin() throws Exception {
        mockMvc.perform(patch("/api/categories/1/activate")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void activate_shouldReturnForbidden_whenUserIsNotAdmin() throws Exception {
        mockMvc.perform(patch("/api/categories/1/activate")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
