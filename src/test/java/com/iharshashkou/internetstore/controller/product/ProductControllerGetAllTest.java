package com.iharshashkou.internetstore.controller.product;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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

    @Test
    void findAll_shouldReturnBadRequest_whenCategoryIdIsNotANumber() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("categoryId", "abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    void findAll_shouldUseDefaultPage_whenPageIsNotANumber() throws Exception {
        when(productService.searchProducts(
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        mockMvc.perform(get("/api/products")
                        .param("page", "abc"))
                .andExpect(status().isOk());

        var pageableCaptor =
                org.mockito.ArgumentCaptor.forClass(Pageable.class);

        verify(productService).searchProducts(
                isNull(),
                isNull(),
                pageableCaptor.capture()
        );

        assertEquals(0, pageableCaptor.getValue().getPageNumber());
    }

    @Test
    void findAll_shouldUseDefaultPageSize_whenSizeIsNotANumber() throws Exception {
        when(productService.searchProducts(
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        mockMvc.perform(get("/api/products")
                        .param("size", "abc"))
                .andExpect(status().isOk());

        var pageableCaptor =
                org.mockito.ArgumentCaptor.forClass(Pageable.class);

        verify(productService).searchProducts(
                isNull(),
                isNull(),
                pageableCaptor.capture()
        );

        assertEquals(12, pageableCaptor.getValue().getPageSize());
    }

    @Test
    void findAll_shouldUseDefaultPageSize_whenPageSizeIsNegative() throws Exception {
        when(productService.searchProducts(
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        mockMvc.perform(get("/api/products")
                        .param("size", "-1"))
                .andExpect(status().isOk());

        var pageableCaptor =
                org.mockito.ArgumentCaptor.forClass(Pageable.class);

        verify(productService).searchProducts(
                isNull(),
                isNull(),
                pageableCaptor.capture()
        );

        assertEquals(12, pageableCaptor.getValue().getPageSize());
    }

    @Test
    void findAll_shouldAcceptBlankSearch() throws Exception {
        when(productService.searchProducts(
                isNull(),
                eq(""),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        mockMvc.perform(get("/api/products")
                        .param("search", ""))
                .andExpect(status().isOk());

        verify(productService).searchProducts(
                isNull(),
                eq(""),
                any(Pageable.class)
        );
    }

    @Test
    void findAll_shouldPreserveSearchWhitespace() throws Exception {
        when(productService.searchProducts(
                isNull(),
                eq("  table  "),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        mockMvc.perform(get("/api/products")
                        .param("search", "  table  "))
                .andExpect(status().isOk());

        verify(productService).searchProducts(
                isNull(),
                eq("  table  "),
                any(Pageable.class)
        );
    }

    @Test
    void findAll_shouldSupportMultipleSortParameters() throws Exception {
        when(productService.searchProducts(
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        mockMvc.perform(get("/api/products")
                        .param("sort", "name,asc", "price,desc"))
                .andExpect(status().isOk());

        var pageableCaptor =
                org.mockito.ArgumentCaptor.forClass(Pageable.class);

        verify(productService).searchProducts(
                isNull(),
                isNull(),
                pageableCaptor.capture()
        );

        var sort = pageableCaptor.getValue().getSort();

        assertEquals(2, sort.stream().count());
        assertEquals("name", sort.toList().get(0).getProperty());
        assertEquals("price", sort.toList().get(1).getProperty());
    }

    @Test
    void findAll_shouldBeAccessibleWithoutAuthentication() throws Exception {
        when(productService.searchProducts(
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());

        verify(productService).searchProducts(
                isNull(),
                isNull(),
                any(Pageable.class)
        );
    }
}
