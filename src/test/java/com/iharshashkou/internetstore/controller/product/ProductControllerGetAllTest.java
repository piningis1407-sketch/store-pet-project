package com.iharshashkou.internetstore.controller.product;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerGetAllTest extends BaseProductControllerTest {

    @Test
    void findAll_shouldReturnProductsWithDefaultPagination() throws Exception {
        when(productService.searchProducts(
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.isNull(),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());

        var pageableCaptor =
                org.mockito.ArgumentCaptor.forClass(Pageable.class);

        verify(productService).searchProducts(
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.isNull(),
                pageableCaptor.capture()
        );

        Pageable pageable = pageableCaptor.getValue();

        assertEquals(0, pageable.getPageNumber());
        assertEquals(12, pageable.getPageSize());
        assertEquals("name", pageable.getSort().iterator().next().getProperty());
        assertEquals(
                org.springframework.data.domain.Sort.Direction.ASC,
                pageable.getSort().iterator().next().getDirection()
        );
    }

    @Test
    void findAll_shouldApplySearchCategoryAndPagination() throws Exception {
        when(productService.searchProducts(
                org.mockito.ArgumentMatchers.eq(3L),
                org.mockito.ArgumentMatchers.eq("table"),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        mockMvc.perform(get("/api/products")
                        .param("categoryId", "3")
                        .param("search", "table")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "price,desc"))
                .andExpect(status().isOk());

        var pageableCaptor =
                org.mockito.ArgumentCaptor.forClass(Pageable.class);

        verify(productService).searchProducts(
                org.mockito.ArgumentMatchers.eq(3L),
                org.mockito.ArgumentMatchers.eq("table"),
                pageableCaptor.capture()
        );

        Pageable pageable = pageableCaptor.getValue();

        assertEquals(1, pageable.getPageNumber());
        assertEquals(5, pageable.getPageSize());
        assertEquals("price", pageable.getSort().iterator().next().getProperty());
        assertEquals(
                org.springframework.data.domain.Sort.Direction.DESC,
                pageable.getSort().iterator().next().getDirection()
        );
    }
}
