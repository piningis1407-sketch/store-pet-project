package com.iharshashkou.internetstore.service.category;

import com.iharshashkou.internetstore.entity.Category;
import com.iharshashkou.internetstore.exception.CategoryHasActiveProductsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceDeactivateTest extends BaseCategoryServiceTest {

    @Test
    void deactivate_shouldDeactivateCategorySuccessfully() {
        Category category = Category.builder()
                .id(1L)
                .name("Столы")
                .active(true)
                .build();

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.existsByCategory_IdAndActiveTrue(1L))
                .thenReturn(false);

        categoryService.deactivate(1L);

        assertFalse(category.isActive());

        verify(categoryRepository).save(category);
    }

    @Test
    void deactivate_shouldThrowExceptionWhenCategoryNotFound() {
        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.deactivate(1L)
        );

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deactivate_shouldThrowExceptionWhenCategoryIsInactive() {
        Category category = Category.builder()
                .id(1L)
                .name("Столы")
                .active(false)
                .build();

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.deactivate(1L)
        );

        verify(productRepository, never())
                .existsByCategory_IdAndActiveTrue(anyLong());

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deactivate_shouldThrowExceptionWhenCategoryHasActiveProducts() {
        Category category = Category.builder()
                .id(1L)
                .name("Столы")
                .active(true)
                .build();

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.existsByCategory_IdAndActiveTrue(1L))
                .thenReturn(true);

        assertThrows(
                CategoryHasActiveProductsException.class,
                () -> categoryService.deactivate(1L)
        );

        assertTrue(category.isActive());

        verify(categoryRepository, never()).save(any(Category.class));
    }
}
