package com.iharshashkou.internetstore.service.product;

import com.iharshashkou.internetstore.entity.Category;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import com.iharshashkou.internetstore.DTO.product.ProductResponse;
import com.iharshashkou.internetstore.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceSearchTest extends BaseProductServiceTest {

    @Test
    void searchProducts_shouldReturnMatchingProducts_whenSearchIsProvided() {
        String search = "Oak";
        PageRequest pageable = PageRequest.of(0, 10);

        Product product = Product.builder()
                .id(1L)
                .name("Oak Table")
                .description("Solid oak table")
                .price(new BigDecimal("500.00"))
                .stockQuantity(5)
                .imageUrl("image.jpg")
                .active(true)
                .build();

        when(productRepository.searchActiveProducts(null, search, pageable))
                .thenReturn(new PageImpl<>(List.of(product)));

        Page<ProductResponse> result = productService.searchProducts(null, search, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Oak Table", result.getContent().getFirst().name());

        verify(productRepository).searchActiveProducts(null, search, pageable);
        verifyNoInteractions(categoryRepository);
    }

    @Test
    void searchProducts_shouldTrimSearchText() {
        String search = "  Oak  ";
        String normalizedSearch = "Oak";
        PageRequest pageable = PageRequest.of(0, 10);

        Product product = Product.builder()
                .id(1L)
                .name("Oak Table")
                .active(true)
                .build();

        when(productRepository.searchActiveProducts(null, normalizedSearch, pageable))
                .thenReturn(new PageImpl<>(List.of(product)));

        Page<ProductResponse> result = productService.searchProducts(null, search, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Oak Table", result.getContent().getFirst().name());

        verify(productRepository).searchActiveProducts(null, normalizedSearch, pageable);
        verifyNoInteractions(categoryRepository);
    }

    @Test
    void searchProducts_shouldReturnAllActiveProducts_whenSearchIsBlank() {
        PageRequest pageable = PageRequest.of(0, 10);

        Product product = Product.builder()
                .id(1L)
                .name("Oak Table")
                .active(true)
                .build();

        when(productRepository.findByActiveTrue(pageable))
                .thenReturn(new PageImpl<>(List.of(product)));

        Page<ProductResponse> result = productService.searchProducts(null, "   ", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Oak Table", result.getContent().getFirst().name());

        verify(productRepository).findByActiveTrue(pageable);
        verify(productRepository, never())
                .searchActiveProducts(any(), any(), any());
        verifyNoInteractions(categoryRepository);
    }

    @Test
    void searchProducts_shouldReturnProductsByCategory_whenCategoryAndSearchAreProvided() {
        Long categoryId = 1L;
        String search = "Oak";
        PageRequest pageable = PageRequest.of(0, 10);

        Category category = Category.builder()
                .id(categoryId)
                .name("Tables")
                .active(true)
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Oak Table")
                .active(true)
                .category(category)
                .build();

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));
        when(productRepository.searchActiveProducts(categoryId, search, pageable))
                .thenReturn(new PageImpl<>(List.of(product)));

        Page<ProductResponse> result =
                productService.searchProducts(categoryId, search, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Oak Table", result.getContent().getFirst().name());
        assertEquals("Tables", result.getContent().getFirst().category().name());

        verify(categoryRepository).findById(categoryId);
        verify(productRepository).searchActiveProducts(categoryId, search, pageable);
    }

    @Test
    void searchProducts_shouldReturnCategoryProducts_whenSearchIsBlank() {
        Long categoryId = 1L;
        PageRequest pageable = PageRequest.of(0, 10);

        Category category = Category.builder()
                .id(categoryId)
                .name("Tables")
                .active(true)
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Oak Table")
                .active(true)
                .category(category)
                .build();

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));
        when(productRepository.findByActiveTrueAndCategory_Id(categoryId, pageable))
                .thenReturn(new PageImpl<>(List.of(product)));

        Page<ProductResponse> result =
                productService.searchProducts(categoryId, "   ", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Oak Table", result.getContent().getFirst().name());

        verify(categoryRepository).findById(categoryId);
        verify(productRepository).findByActiveTrueAndCategory_Id(categoryId, pageable);
        verify(productRepository, never())
                .searchActiveProducts(any(), any(), any());
    }

    @Test
    void searchProducts_shouldThrowException_whenCategoryIsInactive() {
        Long categoryId = 1L;
        PageRequest pageable = PageRequest.of(0, 10);

        Category category = Category.builder()
                .id(categoryId)
                .name("Tables")
                .active(false)
                .build();

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> productService.searchProducts(categoryId, "Oak", pageable)
        );

        assertEquals("Active category not found: " + categoryId, exception.getMessage());

        verify(categoryRepository).findById(categoryId);
        verifyNoInteractions(productRepository);
    }

    @Test
    void searchProducts_shouldThrowException_whenCategoryDoesNotExist() {
        Long categoryId = 99L;
        PageRequest pageable = PageRequest.of(0, 10);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> productService.searchProducts(categoryId, "Oak", pageable)
        );

        assertEquals("Active category not found: " + categoryId, exception.getMessage());

        verify(categoryRepository).findById(categoryId);
        verifyNoInteractions(productRepository);
    }

    @Test
    void searchProducts_shouldReturnAllActiveProducts_whenSearchIsNull() {
        PageRequest pageable = PageRequest.of(0, 10);

        Product product = Product.builder()
                .id(1L)
                .name("Oak Table")
                .active(true)
                .build();

        when(productRepository.findByActiveTrue(pageable))
                .thenReturn(new PageImpl<>(List.of(product)));

        Page<ProductResponse> result =
                productService.searchProducts(null, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Oak Table", result.getContent().getFirst().name());

        verify(productRepository).findByActiveTrue(pageable);
        verifyNoInteractions(categoryRepository);

        verify(productRepository, never())
                .searchActiveProducts(any(), any(), any());
    }
}
