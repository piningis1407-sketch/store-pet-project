package com.iharshashkou.internetstore.service.product;

import org.junit.jupiter.api.Test;
import com.iharshashkou.internetstore.DTO.product.ProductResponse;
import com.iharshashkou.internetstore.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceFindAllForAdminTest extends BaseProductServiceTest {

    @Test
    void findAllForAdmin_shouldReturnAllProducts() {
        PageRequest pageable = PageRequest.of(0, 10);

        Product activeProduct = Product.builder()
                .id(1L)
                .name("Oak Table")
                .price(new BigDecimal("500.00"))
                .active(true)
                .build();

        Product inactiveProduct = Product.builder()
                .id(2L)
                .name("Walnut Table")
                .price(new BigDecimal("700.00"))
                .active(false)
                .build();

        when(productRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(activeProduct, inactiveProduct)));

        Page<ProductResponse> result = productService.findAllForAdmin(pageable);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().getFirst().active());
        assertFalse(result.getContent().get(1).active());
        assertEquals("Walnut Table", result.getContent().get(1).name());

        verify(productRepository).findAll(pageable);
        verify(productRepository, never()).findByActiveTrue(any());
    }
}
