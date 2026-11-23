package com.iharshashkou.internetstore.service.order;

import com.iharshashkou.internetstore.entity.Order;
import com.iharshashkou.internetstore.entity.OrderItem;
import com.iharshashkou.internetstore.entity.OrderStatus;
import com.iharshashkou.internetstore.entity.Product;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceUpdateStatusTest extends BaseOrderServiceTest {

    @Test
    void updateOrderStatus_shouldUpdateStatusSuccessfully() {
        Order order = Order.builder()
                .id(1L)
                .recipientName("Иван Иванов")
                .phone("+79991234567")
                .deliveryAddress("Москва")
                .status(OrderStatus.NEW)
                .totalPrice(new BigDecimal("200.00"))
                .createdAt(Instant.now())
                .build();

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(orderItemRepository.findAllByOrder_Id(1L))
                .thenReturn(List.of());

        var result = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

        assertNotNull(result);
        assertEquals(OrderStatus.CONFIRMED, result.status());

        verify(orderRepository).findById(1L);
        verify(orderRepository).save(order);
        verify(orderItemRepository).findAllByOrder_Id(1L);
    }

    @Test
    void updateOrderStatus_shouldReturnOrderWithoutSavingWhenStatusIsUnchanged() {
        Order order = Order.builder()
                .id(1L)
                .recipientName("Иван Иванов")
                .phone("+79991234567")
                .deliveryAddress("Москва")
                .status(OrderStatus.NEW)
                .totalPrice(new BigDecimal("200.00"))
                .createdAt(Instant.now())
                .build();

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderItemRepository.findAllByOrder_Id(1L))
                .thenReturn(List.of());

        var result = orderService.updateOrderStatus(1L, OrderStatus.NEW);

        assertNotNull(result);
        assertEquals(OrderStatus.NEW, result.status());

        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository).findAllByOrder_Id(1L);
    }

    @Test
    void updateOrderStatus_shouldMoveConfirmedOrderToProcessing() {
        Order order = Order.builder()
                .id(1L)
                .recipientName("Иван Иванов")
                .phone("+79991234567")
                .deliveryAddress("Москва")
                .status(OrderStatus.CONFIRMED)
                .totalPrice(new BigDecimal("200.00"))
                .createdAt(Instant.now())
                .build();

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(orderItemRepository.findAllByOrder_Id(1L))
                .thenReturn(List.of());

        var result = orderService.updateOrderStatus(1L, OrderStatus.PROCESSING);

        assertEquals(OrderStatus.PROCESSING, result.status());

        verify(orderRepository).save(order);
        verify(orderItemRepository).findAllByOrder_Id(1L);
    }

    @Test
    void updateOrderStatus_shouldMoveProcessingOrderToShipped() {
        Order order = Order.builder()
                .id(1L)
                .recipientName("Иван Иванов")
                .phone("+79991234567")
                .deliveryAddress("Москва")
                .status(OrderStatus.PROCESSING)
                .totalPrice(new BigDecimal("200.00"))
                .createdAt(Instant.now())
                .build();

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(orderItemRepository.findAllByOrder_Id(1L))
                .thenReturn(List.of());

        var result = orderService.updateOrderStatus(1L, OrderStatus.SHIPPED);

        assertEquals(OrderStatus.SHIPPED, result.status());

        verify(orderRepository).save(order);
        verify(orderItemRepository).findAllByOrder_Id(1L);
    }

    @Test
    void updateOrderStatus_shouldMoveShippedOrderToCompleted() {
        Order order = Order.builder()
                .id(1L)
                .recipientName("Иван Иванов")
                .phone("+79991234567")
                .deliveryAddress("Москва")
                .status(OrderStatus.SHIPPED)
                .totalPrice(new BigDecimal("200.00"))
                .createdAt(Instant.now())
                .build();

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(orderItemRepository.findAllByOrder_Id(1L))
                .thenReturn(List.of());

        var result = orderService.updateOrderStatus(1L, OrderStatus.COMPLETED);

        assertEquals(OrderStatus.COMPLETED, result.status());

        verify(orderRepository).save(order);
        verify(orderItemRepository).findAllByOrder_Id(1L);
    }

    @Test
    void updateOrderStatus_shouldThrowExceptionForInvalidTransition() {
        Order order = Order.builder()
                .id(1L)
                .recipientName("Иван Иванов")
                .phone("+79991234567")
                .deliveryAddress("Москва")
                .status(OrderStatus.NEW)
                .totalPrice(new BigDecimal("200.00"))
                .createdAt(Instant.now())
                .build();

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class,
                () -> orderService.updateOrderStatus(1L, OrderStatus.PROCESSING));

        assertEquals(OrderStatus.NEW, order.getStatus());

        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
        verifyNoInteractions(orderItemRepository, productRepository);
    }

    @Test
    void updateOrderStatus_shouldThrowExceptionWhenOrderNotFound() {
        when(orderRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED));

        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
        verifyNoInteractions(orderItemRepository, productRepository);
    }

    @Test
    void updateOrderStatus_shouldCancelOrderAndRestoreProductStock() {
        Order order = Order.builder()
                .id(1L)
                .recipientName("Иван Иванов")
                .phone("+79991234567")
                .deliveryAddress("Москва")
                .status(OrderStatus.CONFIRMED)
                .totalPrice(new BigDecimal("200.00"))
                .createdAt(Instant.now())
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Стол из дерева и смолы")
                .stockQuantity(5)
                .active(true)
                .build();

        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .order(order)
                .product(product)
                .productName(product.getName())
                .price(new BigDecimal("200.00"))
                .quantity(2)
                .subtotal(new BigDecimal("400.00"))
                .build();

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderItemRepository.findAllByOrder_Id(1L))
                .thenReturn(List.of(orderItem));

        when(productRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(product));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = orderService.updateOrderStatus(1L, OrderStatus.CANCELLED);

        assertEquals(OrderStatus.CANCELLED, result.status());
        assertEquals(7, product.getStockQuantity());

        verify(productRepository).findByIdForUpdate(1L);
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderStatus_shouldThrowExceptionWhenProductNotFoundDuringCancellation() {
        Order order = Order.builder()
                .id(1L)
                .recipientName("Иван Иванов")
                .phone("+79991234567")
                .deliveryAddress("Москва")
                .status(OrderStatus.CONFIRMED)
                .totalPrice(new BigDecimal("200.00"))
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Стол из дерева и смолы")
                .stockQuantity(5)
                .build();

        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .order(order)
                .product(product)
                .quantity(2)
                .build();

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderItemRepository.findAllByOrder_Id(1L))
                .thenReturn(List.of(orderItem));

        when(productRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> orderService.updateOrderStatus(1L, OrderStatus.CANCELLED));

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        assertEquals(5, product.getStockQuantity());

        verify(productRepository).findByIdForUpdate(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_shouldCancelNewOrderAndRestoreProductStock() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.NEW)
                .totalPrice(new BigDecimal("400.00"))
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Стол из дерева и смолы")
                .stockQuantity(3)
                .build();

        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .order(order)
                .product(product)
                .quantity(2)
                .build();

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderItemRepository.findAllByOrder_Id(1L))
                .thenReturn(List.of(orderItem));

        when(productRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(product));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = orderService.updateOrderStatus(1L, OrderStatus.CANCELLED);

        assertEquals(OrderStatus.CANCELLED, result.status());
        assertEquals(5, product.getStockQuantity());

        verify(productRepository).findByIdForUpdate(1L);
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderStatus_shouldThrowExceptionWhenCancellingCompletedOrder() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.COMPLETED)
                .totalPrice(new BigDecimal("200.00"))
                .build();

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class,
                () -> orderService.updateOrderStatus(1L, OrderStatus.CANCELLED));

        assertEquals(OrderStatus.COMPLETED, order.getStatus());

        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
        verifyNoInteractions(orderItemRepository, productRepository);
    }

    @Test
    void updateOrderStatus_shouldThrowExceptionWhenUpdatingCancelledOrder() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.CANCELLED)
                .totalPrice(new BigDecimal("200.00"))
                .build();

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class,
                () -> orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED));

        assertEquals(OrderStatus.CANCELLED, order.getStatus());

        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
        verifyNoInteractions(orderItemRepository, productRepository);
    }

    @Test
    void updateOrderStatus_shouldThrowExceptionWhenMovingShippedOrderBackToProcessing() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.SHIPPED)
                .totalPrice(new BigDecimal("200.00"))
                .build();

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class,
                () -> orderService.updateOrderStatus(1L, OrderStatus.PROCESSING));

        assertEquals(OrderStatus.SHIPPED, order.getStatus());

        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
        verifyNoInteractions(orderItemRepository, productRepository);
    }

    @Test
    void updateOrderStatus_shouldRestoreStockForMultipleProducts() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.PROCESSING)
                .totalPrice(new BigDecimal("600.00"))
                .build();

        Product firstProduct = Product.builder()
                .id(1L)
                .name("Стол")
                .stockQuantity(3)
                .build();

        Product secondProduct = Product.builder()
                .id(2L)
                .name("Тумба")
                .stockQuantity(5)
                .build();

        OrderItem firstItem = OrderItem.builder()
                .id(1L)
                .order(order)
                .product(firstProduct)
                .quantity(2)
                .build();

        OrderItem secondItem = OrderItem.builder()
                .id(2L)
                .order(order)
                .product(secondProduct)
                .quantity(4)
                .build();

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderItemRepository.findAllByOrder_Id(1L))
                .thenReturn(List.of(firstItem, secondItem));

        when(productRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(firstProduct));

        when(productRepository.findByIdForUpdate(2L))
                .thenReturn(Optional.of(secondProduct));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = orderService.updateOrderStatus(1L, OrderStatus.CANCELLED);

        assertEquals(OrderStatus.CANCELLED, result.status());
        assertEquals(5, firstProduct.getStockQuantity());
        assertEquals(9, secondProduct.getStockQuantity());

        verify(productRepository).findByIdForUpdate(1L);
        verify(productRepository).findByIdForUpdate(2L);
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderStatus_shouldNotChangeProductStockWhenOrderIsNotCancelled() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.NEW)
                .totalPrice(new BigDecimal("200.00"))
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Стол")
                .stockQuantity(5)
                .build();

        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .order(order)
                .product(product)
                .quantity(2)
                .build();

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderItemRepository.findAllByOrder_Id(1L))
                .thenReturn(List.of(orderItem));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

        assertEquals(OrderStatus.CONFIRMED, result.status());
        assertEquals(5, product.getStockQuantity());

        verify(orderRepository).save(order);
        verifyNoInteractions(productRepository);
    }

    @Test
    void updateOrderStatus_shouldCancelOrderWithoutItems() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.NEW)
                .totalPrice(BigDecimal.ZERO)
                .build();

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderItemRepository.findAllByOrder_Id(1L))
                .thenReturn(List.of());

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = orderService.updateOrderStatus(1L, OrderStatus.CANCELLED);

        assertEquals(OrderStatus.CANCELLED, result.status());
        assertTrue(result.items().isEmpty());

        verify(orderRepository).save(order);
        verify(orderItemRepository, times(2)).findAllByOrder_Id(1L);
        verifyNoInteractions(productRepository);
    }

    @Test
    void updateOrderStatus_shouldNotRestoreStockWhenOrderIsAlreadyCancelled() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.CANCELLED)
                .totalPrice(new BigDecimal("200.00"))
                .build();

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderItemRepository.findAllByOrder_Id(1L))
                .thenReturn(List.of());

        var result = orderService.updateOrderStatus(1L, OrderStatus.CANCELLED);

        assertEquals(OrderStatus.CANCELLED, result.status());

        verify(orderRepository, never()).save(any(Order.class));
        verifyNoInteractions(productRepository);
    }
}
