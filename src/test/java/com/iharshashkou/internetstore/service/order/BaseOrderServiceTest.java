package com.iharshashkou.internetstore.service.order;

import com.iharshashkou.internetstore.repository.*;
import com.iharshashkou.internetstore.service.OrderService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class BaseOrderServiceTest {

    @Mock
    protected OrderRepository orderRepository;

    @Mock
    protected OrderItemRepository orderItemRepository;

    @Mock
    protected CartRepository cartRepository;

    @Mock
    protected CartItemRepository cartItemRepository;

    @Mock
    protected ProductRepository productRepository;

    @Mock
    protected UserRepository userRepository;

    @InjectMocks
    protected OrderService orderService;
}
