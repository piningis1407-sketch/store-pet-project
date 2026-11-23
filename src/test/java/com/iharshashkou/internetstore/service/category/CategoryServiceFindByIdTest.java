package com.iharshashkou.internetstore.service.category;

import com.iharshashkou.internetstore.entity.Category;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceFindByIdTest extends BaseCategoryServiceTest {

    @Test
    void findById_shouldReturnActiveCategory() {
        Category category = Category.builder()
                .id(1L)
                .name("Столы")
                .description("Столы из дерева")
                .active(true)
                .build();

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        var result = categoryService.findById(1L);

        assertEquals(1L, result.id());
        assertEquals("Столы", result.name());
        assertEquals("Столы из дерева", result.description());
        assertTrue(result.active());

        verify(categoryRepository).findById(1L);
    }

    @Test
    void findById_shouldThrowExceptionWhenCategoryNotFound() {
        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.findById(1L)
        );
    }

    @Test
    void findById_shouldThrowExceptionWhenCategoryIsInactive() {
        Category category = Category.builder()
                .id(1L)
                .name("Столы")
                .active(false)
                .build();

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.findById(1L)
        );
    }
}
