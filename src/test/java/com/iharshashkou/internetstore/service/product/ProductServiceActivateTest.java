package com.iharshashkou.internetstore.service.product;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import com.iharshashkou.internetstore.entity.Product;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceActivateTest extends BaseProductServiceTest {

    @Test
    void activate_shouldSetProductActive_whenProductExists() {
        Long productId = 1L;

        Product product = Product.builder()
                .id(productId)
                .name("Oak Table")
                .active(false)
                .build();

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));
        when(productRepository.save(product))
                .thenReturn(product);

        productService.activate(productId);

        assertTrue(product.isActive());

        verify(productRepository).findById(productId);
        verify(productRepository).save(product);
    }

    @Test
    void activate_shouldThrowException_whenProductDoesNotExist() {
        Long productId = 99L;

        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> productService.activate(productId)
        );

        assertEquals("Product not found: " + productId, exception.getMessage());

        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }
}
