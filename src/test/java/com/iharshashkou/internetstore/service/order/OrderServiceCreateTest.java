package com.iharshashkou.internetstore.service.order;

import com.iharshashkou.internetstore.entity.Order;
import com.iharshashkou.internetstore.entity.OrderItem;
import com.iharshashkou.internetstore.entity.OrderStatus;
import com.iharshashkou.internetstore.entity.Product;
import com.iharshashkou.internetstore.entity.User;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import com.iharshashkou.internetstore.DTO.order.CreateOrderRequest;
import com.iharshashkou.internetstore.entity.Cart;
import com.iharshashkou.internetstore.entity.CartItem;
import com.iharshashkou.internetstore.exception.CartConflictException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceCreateTest extends BaseOrderServiceTest {

    @Test
    void createOrder_shouldThrowExceptionWhenCartNotFound() {
        when(cartRepository.findByUserIdForUpdate(1L))
                .thenReturn(Optional.empty());

        CreateOrderRequest request = new CreateOrderRequest(
                "Иван Иванов",
                "+79991234567",
                "Москва, ул. Лесная, 10"
        );

        assertThrows(
                CartConflictException.class,
                () -> orderService.createOrder(1L, request)
        );

        verifyNoInteractions(
                cartItemRepository,
                userRepository,
                orderRepository,
                orderItemRepository,
                productRepository
        );
    }

    @Test
    void createOrder_shouldThrowExceptionWhenCartIsEmpty() {
        Cart cart = Cart.builder()
                .id(1L)
                .build();

        when(cartRepository.findByUserIdForUpdate(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findAllByCart_Id(1L))
                .thenReturn(List.of());

        CreateOrderRequest request = new CreateOrderRequest(
                "Иван Иванов",
                "+79991234567",
                "Москва, ул. Лесная, 10"
        );

        assertThrows(
                CartConflictException.class,
                () -> orderService.createOrder(1L, request)
        );

        verifyNoInteractions(
                userRepository,
                orderRepository,
                orderItemRepository,
                productRepository
        );
    }

    @Test
    void createOrder_shouldThrowExceptionWhenUserNotFound() {
        Cart cart = Cart.builder()
                .id(1L)
                .build();

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .quantity(2)
                .build();

        when(cartRepository.findByUserIdForUpdate(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findAllByCart_Id(1L))
                .thenReturn(List.of(cartItem));

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        CreateOrderRequest request = new CreateOrderRequest(
                "Иван Иванов",
                "+79991234567",
                "Москва, ул. Лесная, 10"
        );

        assertThrows(
                EntityNotFoundException.class,
                () -> orderService.createOrder(1L, request)
        );

        verifyNoInteractions(
                orderRepository,
                orderItemRepository,
                productRepository
        );
    }

    @Test
    void createOrder_shouldCreateOrderSuccessfully() {
        Cart cart = Cart.builder()
                .id(1L)
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Стол из дуба")
                .price(new BigDecimal("100.00"))
                .stockQuantity(10)
                .active(true)
                .build();

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        User user = User.builder()
                .id(1L)
                .name("Иван Иванов")
                .build();

        Order order = Order.builder()
                .id(1L)
                .build();

        when(cartRepository.findByUserIdForUpdate(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findAllByCart_Id(1L))
                .thenReturn(List.of(cartItem));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order savedOrder = invocation.getArgument(0);
                    savedOrder.setId(1L);
                    return savedOrder;
                });

        when(productRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(product));

        when(orderItemRepository.findAllByOrder_Id(1L))
                .thenReturn(List.of(
                        OrderItem.builder()
                                .product(product)
                                .productName("Стол из дуба")
                                .price(new BigDecimal("100.00"))
                                .quantity(2)
                                .subtotal(new BigDecimal("200.00"))
                                .build()
                ));

        CreateOrderRequest request = new CreateOrderRequest(
                " Иван Иванов ",
                " +79991234567 ",
                " Москва, ул. Лесная, 10 "
        );

        var result = orderService.createOrder(1L, request);

        assertEquals(1L, result.id());
        assertEquals("Иван Иванов", result.recipientName());
        assertEquals("+79991234567", result.phone());
        assertEquals("Москва, ул. Лесная, 10", result.deliveryAddress());
        assertEquals(OrderStatus.NEW, result.status());
        assertEquals(new BigDecimal("200.00"), result.totalPrice());

        assertEquals(8, product.getStockQuantity());

        assertEquals(1, result.items().size());
        assertEquals(2, result.items().getFirst().quantity());
        assertEquals(new BigDecimal("200.00"), result.items().getFirst().subtotal());

        verify(orderItemRepository).save(argThat(item ->
                item.getProductName().equals("Стол из дуба")
                        && item.getQuantity() == 2
                        && item.getSubtotal().equals(new BigDecimal("200.00"))
        ));

        verify(cartItemRepository).deleteAllByCart_Id(1L);
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    void createOrder_shouldThrowExceptionWhenProductNotFound() {
        Cart cart = Cart.builder()
                .id(1L)
                .build();

        Product product = Product.builder()
                .id(1L)
                .build();

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        User user = User.builder()
                .id(1L)
                .build();

        when(cartRepository.findByUserIdForUpdate(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findAllByCart_Id(1L))
                .thenReturn(List.of(cartItem));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(1L);
                    return order;
                });

        when(productRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.empty());

        CreateOrderRequest request = new CreateOrderRequest(
                "Иван Иванов",
                "+79991234567",
                "Москва, ул. Лесная, 10"
        );

        assertThrows(
                EntityNotFoundException.class,
                () -> orderService.createOrder(1L, request)
        );

        verify(orderItemRepository, never()).save(any(OrderItem.class));
        verify(cartItemRepository, never()).deleteAllByCart_Id(anyLong());
    }

    @Test
    void createOrder_shouldThrowExceptionWhenProductIsInactive() {
        Cart cart = Cart.builder()
                .id(1L)
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Стол из дуба")
                .stockQuantity(10)
                .active(false)
                .build();

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        User user = User.builder()
                .id(1L)
                .build();

        when(cartRepository.findByUserIdForUpdate(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findAllByCart_Id(1L))
                .thenReturn(List.of(cartItem));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(1L);
                    return order;
                });

        when(productRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(product));

        CreateOrderRequest request = new CreateOrderRequest(
                "Иван Иванов",
                "+79991234567",
                "Москва, ул. Лесная, 10"
        );

        assertThrows(
                CartConflictException.class,
                () -> orderService.createOrder(1L, request)
        );

        verify(orderItemRepository, never()).save(any(OrderItem.class));
        verify(cartItemRepository, never()).deleteAllByCart_Id(1L);
    }

    @Test
    void createOrder_shouldThrowExceptionWhenStockIsInsufficient() {
        Cart cart = Cart.builder()
                .id(1L)
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Стол из дуба")
                .stockQuantity(1)
                .active(true)
                .build();

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        User user = User.builder()
                .id(1L)
                .build();

        when(cartRepository.findByUserIdForUpdate(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findAllByCart_Id(1L))
                .thenReturn(List.of(cartItem));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(1L);
                    return order;
                });

        when(productRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(product));

        CreateOrderRequest request = new CreateOrderRequest(
                "Иван Иванов",
                "+79991234567",
                "Москва, ул. Лесная, 10"
        );

        assertThrows(
                CartConflictException.class,
                () -> orderService.createOrder(1L, request)
        );

        assertEquals(1, product.getStockQuantity());

        verify(orderItemRepository, never()).save(any(OrderItem.class));
        verify(cartItemRepository, never()).deleteAllByCart_Id(1L);
    }

    @Test
    void createOrder_shouldCalculateTotalForMultipleProducts() {
        Cart cart = Cart.builder()
                .id(1L)
                .build();

        Product firstProduct = Product.builder()
                .id(1L)
                .name("Стол из дуба")
                .price(new BigDecimal("100.00"))
                .stockQuantity(10)
                .active(true)
                .build();

        Product secondProduct = Product.builder()
                .id(2L)
                .name("Стол из ясеня")
                .price(new BigDecimal("150.00"))
                .stockQuantity(8)
                .active(true)
                .build();

        CartItem firstItem = CartItem.builder()
                .cart(cart)
                .product(firstProduct)
                .quantity(2)
                .build();

        CartItem secondItem = CartItem.builder()
                .cart(cart)
                .product(secondProduct)
                .quantity(3)
                .build();

        User user = User.builder()
                .id(1L)
                .build();

        when(cartRepository.findByUserIdForUpdate(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findAllByCart_Id(1L))
                .thenReturn(List.of(firstItem, secondItem));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(1L);
                    return order;
                });

        when(productRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(firstProduct));

        when(productRepository.findByIdForUpdate(2L))
                .thenReturn(Optional.of(secondProduct));

        when(orderItemRepository.findAllByOrder_Id(1L))
                .thenReturn(List.of(
                        OrderItem.builder()
                                .product(firstProduct)
                                .productName("Стол из дуба")
                                .price(new BigDecimal("100.00"))
                                .quantity(2)
                                .subtotal(new BigDecimal("200.00"))
                                .build(),
                        OrderItem.builder()
                                .product(secondProduct)
                                .productName("Стол из ясеня")
                                .price(new BigDecimal("150.00"))
                                .quantity(3)
                                .subtotal(new BigDecimal("450.00"))
                                .build()
                ));

        CreateOrderRequest request = new CreateOrderRequest(
                "Иван Иванов",
                "+79991234567",
                "Москва, ул. Лесная, 10"
        );

        var result = orderService.createOrder(1L, request);

        assertEquals(new BigDecimal("650.00"), result.totalPrice());
        assertEquals(2, result.items().size());

        assertEquals(8, firstProduct.getStockQuantity());
        assertEquals(5, secondProduct.getStockQuantity());

        verify(orderItemRepository, times(2)).save(any(OrderItem.class));
        verify(cartItemRepository).deleteAllByCart_Id(1L);
    }

    @Test
    void createOrder_shouldTrimFieldsAndSetInitialStatus() {
        Cart cart = Cart.builder()
                .id(1L)
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Стол")
                .price(new BigDecimal("100.00"))
                .stockQuantity(5)
                .active(true)
                .build();

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(1)
                .build();

        User user = User.builder()
                .id(1L)
                .build();

        when(cartRepository.findByUserIdForUpdate(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findAllByCart_Id(1L))
                .thenReturn(List.of(cartItem));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(1L);
                    return order;
                });

        when(productRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(product));

        when(orderItemRepository.findAllByOrder_Id(1L))
                .thenReturn(List.of());

        CreateOrderRequest request = new CreateOrderRequest(
                "  Иван Иванов  ",
                "  +79991234567  ",
                "  Москва, ул. Лесная, 10  "
        );

        orderService.createOrder(1L, request);

        verify(orderRepository, times(2)).save(argThat(order ->
                order.getRecipientName().equals("Иван Иванов")
                        && order.getPhone().equals("+79991234567")
                        && order.getDeliveryAddress().equals("Москва, ул. Лесная, 10")
                        && order.getStatus() == OrderStatus.NEW
        ));
    }
}
