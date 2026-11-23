package com.iharshashkou.internetstore.service.product;

import com.iharshashkou.internetstore.repository.CategoryRepository;
import com.iharshashkou.internetstore.repository.ProductRepository;
import com.iharshashkou.internetstore.service.ProductService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class BaseProductServiceTest {

    @Mock
    protected ProductRepository productRepository;

    @Mock
    protected CategoryRepository categoryRepository;

    @InjectMocks
    protected ProductService productService;
}
