package com.iharshashkou.internetstore.service.category;

import com.iharshashkou.internetstore.entity.Category;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceFindAllForAdminTest extends BaseCategoryServiceTest {

    @Test
    void findAllForAdmin_shouldReturnAllCategories() {
        Category activeCategory = Category.builder()
                .id(1L)
                .name("Столы")
                .active(true)
                .build();

        Category inactiveCategory = Category.builder()
                .id(2L)
                .name("Стулья")
                .active(false)
                .build();

        when(categoryRepository.findAllByOrderByNameAsc())
                .thenReturn(List.of(activeCategory, inactiveCategory));

        var result = categoryService.findAllForAdmin();

        assertEquals(2, result.size());
        assertTrue(result.getFirst().active());
        assertFalse(result.get(1).active());

        verify(categoryRepository).findAllByOrderByNameAsc();
    }

    @Test
    void findAllForAdmin_shouldReturnEmptyList() {
        when(categoryRepository.findAllByOrderByNameAsc())
                .thenReturn(List.of());

        var result = categoryService.findAllForAdmin();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(categoryRepository).findAllByOrderByNameAsc();
    }
}
