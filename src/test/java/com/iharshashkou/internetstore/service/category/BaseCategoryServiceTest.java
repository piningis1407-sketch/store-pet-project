package com.iharshashkou.internetstore.service.category;

import com.iharshashkou.internetstore.repository.CategoryRepository;
import com.iharshashkou.internetstore.repository.ProductRepository;
import com.iharshashkou.internetstore.service.CategoryService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class BaseCategoryServiceTest {

    @Mock
    protected CategoryRepository categoryRepository;

    @Mock
    protected ProductRepository productRepository;

    @InjectMocks
    protected CategoryService categoryService;
}
