package com.iharshashkou.internetstore.service.product;

import org.junit.jupiter.api.Test;
import com.iharshashkou.internetstore.entity.Product;
import jakarta.persistence.EntityNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceDeactivateTest extends BaseProductServiceTest {

    @Test
    void deactivate_shouldSetProductInactive_whenProductExists() {
        Long productId = 1L;

        Product product = Product.builder()
                .id(productId)
                .name("Oak Table")
                .active(true)
                .build();

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));
        when(productRepository.save(product))
                .thenReturn(product);

        productService.deactivate(productId);

        assertFalse(product.isActive());

        verify(productRepository).findById(productId);
        verify(productRepository).save(product);
    }

    @Test
    void deactivate_shouldThrowException_whenProductDoesNotExist() {
        Long productId = 99L;

        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> productService.deactivate(productId)
        );

        assertEquals("Product not found: " + productId, exception.getMessage());

        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }
}
