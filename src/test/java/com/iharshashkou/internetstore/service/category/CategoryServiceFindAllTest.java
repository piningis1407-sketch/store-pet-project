package com.iharshashkou.internetstore.service.category;

import com.iharshashkou.internetstore.entity.Category;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceFindAllTest extends BaseCategoryServiceTest {

    @Test
    void findAll_shouldReturnActiveCategories() {
        Category first = Category.builder()
                .id(1L)
                .name("Столы")
                .description("Столы из дерева")
                .active(true)
                .build();

        Category second = Category.builder()
                .id(2L)
                .name("Стулья")
                .description("Стулья из дерева")
                .active(true)
                .build();

        when(categoryRepository.findByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(first, second));

        var result = categoryService.findAll();

        assertEquals(2, result.size());
        assertEquals("Столы", result.getFirst().name());
        assertEquals("Стулья", result.get(1).name());
        assertTrue(result.getFirst().active());

        verify(categoryRepository)
                .findByActiveTrueOrderByNameAsc();
    }

    @Test
    void findAll_shouldReturnEmptyList() {
        when(categoryRepository.findByActiveTrueOrderByNameAsc())
                .thenReturn(List.of());

        var result = categoryService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(categoryRepository)
                .findByActiveTrueOrderByNameAsc();
    }
}
