package com.iharshashkou.internetstore.service.product;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import com.iharshashkou.internetstore.entity.Product;
import com.iharshashkou.internetstore.DTO.product.ProductResponse;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceFindByIdTest extends BaseProductServiceTest {

    @Test
    void findById_shouldReturnProduct_whenProductExistsAndIsActive() {
        Long productId = 1L;

        Product product = Product.builder()
                .id(productId)
                .name("Oak Table")
                .description("Solid oak table")
                .price(new BigDecimal("500.00"))
                .stockQuantity(5)
                .imageUrl("image.jpg")
                .active(true)
                .build();

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        ProductResponse result = productService.findById(productId);

        assertEquals(productId, result.id());
        assertEquals("Oak Table", result.name());
        assertEquals(new BigDecimal("500.00"), result.price());
        assertTrue(result.active());

        verify(productRepository).findById(productId);
    }

    @Test
    void findById_shouldThrowException_whenProductIsInactive() {
        Long productId = 1L;

        Product product = Product.builder()
                .id(productId)
                .name("Oak Table")
                .active(false)
                .build();

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> productService.findById(productId)
        );

        assertEquals("Product not found: " + productId, exception.getMessage());

        verify(productRepository).findById(productId);
    }

    @Test
    void findById_shouldThrowException_whenProductDoesNotExist() {
        Long productId = 99L;

        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> productService.findById(productId)
        );

        assertEquals("Product not found: " + productId, exception.getMessage());

        verify(productRepository).findById(productId);
    }
}
