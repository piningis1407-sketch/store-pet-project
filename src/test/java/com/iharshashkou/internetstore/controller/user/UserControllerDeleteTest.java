package com.iharshashkou.internetstore.controller.user;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerDeleteTest extends BaseUserControllerTest {

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_shouldReturnNoContent_whenUserExists() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/users/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void delete_shouldReturnForbidden_whenUserIsNotAdmin() throws Exception {
        mockMvc.perform(delete("/api/users/1").with(csrf()))
                .andExpect(status().isForbidden());
    }
}
