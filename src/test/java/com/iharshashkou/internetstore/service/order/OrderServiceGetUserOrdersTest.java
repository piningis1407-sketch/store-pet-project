package com.iharshashkou.internetstore.service.order;

import com.iharshashkou.internetstore.entity.Order;
import com.iharshashkou.internetstore.entity.OrderStatus;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceGetUserOrdersTest extends BaseOrderServiceTest {

    @Test
    void getUserOrders_shouldReturnUserOrdersSuccessfully() {
        Order firstOrder = Order.builder()
                .id(1L)
                .recipientName("Иван Иванов")
                .phone("+79991234567")
                .deliveryAddress("Москва")
                .status(OrderStatus.NEW)
                .totalPrice(new BigDecimal("200.00"))
                .createdAt(Instant.now())
                .build();

        Order secondOrder = Order.builder()
                .id(2L)
                .recipientName("Иван Иванов")
                .phone("+79991234567")
                .deliveryAddress("Москва")
                .status(OrderStatus.CONFIRMED)
                .totalPrice(new BigDecimal("300.00"))
                .createdAt(Instant.now())
                .build();

        when(orderRepository.findAllByUser_IdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(firstOrder, secondOrder));

        when(orderItemRepository.findAllByOrder_Id(1L))
                .thenReturn(List.of());

        when(orderItemRepository.findAllByOrder_Id(2L))
                .thenReturn(List.of());

        var result = orderService.getUserOrders(1L);

        assertEquals(2, result.size());

        assertEquals(1L, result.getFirst().id());
        assertEquals(OrderStatus.NEW, result.getFirst().status());
        assertEquals(new BigDecimal("200.00"), result.getFirst().totalPrice());

        assertEquals(2L, result.get(1).id());
        assertEquals(OrderStatus.CONFIRMED, result.get(1).status());
        assertEquals(new BigDecimal("300.00"), result.get(1).totalPrice());

        verify(orderRepository).findAllByUser_IdOrderByCreatedAtDesc(1L);
        verify(orderItemRepository).findAllByOrder_Id(1L);
        verify(orderItemRepository).findAllByOrder_Id(2L);
    }

    @Test
    void getUserOrders_shouldReturnEmptyListWhenNoOrdersExist() {
        when(orderRepository.findAllByUser_IdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of());

        var result = orderService.getUserOrders(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(orderRepository).findAllByUser_IdOrderByCreatedAtDesc(1L);
        verifyNoInteractions(orderItemRepository);
    }

    @Test
    void getUserOrder_shouldReturnOrderSuccessfully() {
        Order order = Order.builder()
                .id(1L)
                .recipientName("Иван Иванов")
                .phone("+79991234567")
                .deliveryAddress("Москва")
                .status(OrderStatus.NEW)
                .totalPrice(new BigDecimal("200.00"))
                .createdAt(Instant.now())
                .build();

        when(orderRepository.findByIdAndUser_Id(1L, 10L))
                .thenReturn(Optional.of(order));

        when(orderItemRepository.findAllByOrder_Id(1L))
                .thenReturn(List.of());

        var result = orderService.getUserOrder(10L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Иван Иванов", result.recipientName());
        assertEquals(OrderStatus.NEW, result.status());
        assertEquals(new BigDecimal("200.00"), result.totalPrice());
        assertTrue(result.items().isEmpty());

        verify(orderRepository).findByIdAndUser_Id(1L, 10L);
        verify(orderItemRepository).findAllByOrder_Id(1L);
    }

    @Test
    void getUserOrder_shouldThrowExceptionWhenOrderNotFound() {
        when(orderRepository.findByIdAndUser_Id(1L, 10L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> orderService.getUserOrder(10L, 1L));

        verify(orderRepository).findByIdAndUser_Id(1L, 10L);
        verifyNoInteractions(orderItemRepository);
    }
}
