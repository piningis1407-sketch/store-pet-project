package com.iharshashkou.internetstore.service.cart;

import com.iharshashkou.internetstore.repository.CartItemRepository;
import com.iharshashkou.internetstore.repository.CartRepository;
import com.iharshashkou.internetstore.repository.ProductRepository;
import com.iharshashkou.internetstore.service.CartService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class BaseCartServiceTest {

    @Mock
    protected CartRepository cartRepository;

    @Mock
    protected CartItemRepository cartItemRepository;

    @Mock
    protected ProductRepository productRepository;

    @InjectMocks
    protected CartService cartService;
}
