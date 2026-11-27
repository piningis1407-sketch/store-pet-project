package com.iharshashkou.internetstore.controller.product;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerGetAllForAdminTest extends BaseProductControllerTest {

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAllForAdmin_shouldReturnProducts() throws Exception {
        when(productService.findAllForAdmin(any(Pageable.class)))
                .thenReturn(Page.empty());

        mockMvc.perform(get("/api/products/admin"))
                .andExpect(status().isOk());

        var pageableCaptor =
                org.mockito.ArgumentCaptor.forClass(Pageable.class);

        verify(productService).findAllForAdmin(pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();

        assertEquals(0, pageable.getPageNumber());
        assertEquals(20, pageable.getPageSize());
    }

    @Test
    @WithMockUser(roles = "USER")
    void findAllForAdmin_shouldReturnForbidden_whenUserIsNotAdmin() throws Exception {
        mockMvc.perform(get("/api/products/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    void findAllForAdmin_shouldReturnUnauthorized_whenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/products/admin"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(productService);
    }


}
