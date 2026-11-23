package com.iharshashkou.internetstore.service.product;

import org.junit.jupiter.api.Test;
import com.iharshashkou.internetstore.entity.Product;
import com.iharshashkou.internetstore.DTO.product.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceFindAllTest extends BaseProductServiceTest {

    @Test
    void findAll_shouldReturnActiveProducts() {
        Product product = Product.builder()
                .id(1L)
                .name("Oak Table")
                .description("Solid oak table")
                .price(new BigDecimal("500.00"))
                .stockQuantity(5)
                .imageUrl("image.jpg")
                .active(true)
                .build();

        PageRequest pageable = PageRequest.of(0, 10);

        when(productRepository.findByActiveTrue(pageable))
                .thenReturn(new PageImpl<>(List.of(product)));

        Page<ProductResponse> result = productService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Oak Table", result.getContent().getFirst().name());
        assertEquals(new BigDecimal("500.00"), result.getContent().getFirst().price());

        verify(productRepository).findByActiveTrue(pageable);
    }

    @Test
    void findAll_shouldReturnEmptyPage_whenNoProductsExist() {
        PageRequest pageable = PageRequest.of(0, 10);

        when(productRepository.findByActiveTrue(pageable))
                .thenReturn(Page.empty(pageable));

        Page<ProductResponse> result = productService.findAll(pageable);

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());

        verify(productRepository).findByActiveTrue(pageable);
    }
}
