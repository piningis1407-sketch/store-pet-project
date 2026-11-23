package com.iharshashkou.internetstore.service.order;

import com.iharshashkou.internetstore.entity.Order;
import com.iharshashkou.internetstore.entity.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceGetAllOrdersTest extends BaseOrderServiceTest {

    @Test
    void getAllOrders_shouldReturnAllOrdersSuccessfully() {
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
                .recipientName("Петр Петров")
                .phone("+79997654321")
                .deliveryAddress("Санкт-Петербург")
                .status(OrderStatus.CONFIRMED)
                .totalPrice(new BigDecimal("300.00"))
                .createdAt(Instant.now())
                .build();

        when(orderRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(firstOrder, secondOrder));

        when(orderItemRepository.findAllByOrder_Id(1L))
                .thenReturn(List.of());

        when(orderItemRepository.findAllByOrder_Id(2L))
                .thenReturn(List.of());

        var result = orderService.getAllOrders();

        assertEquals(2, result.size());

        assertEquals(1L, result.getFirst().id());
        assertEquals("Иван Иванов", result.getFirst().recipientName());
        assertEquals(OrderStatus.NEW, result.getFirst().status());

        assertEquals(2L, result.get(1).id());
        assertEquals("Петр Петров", result.get(1).recipientName());
        assertEquals(OrderStatus.CONFIRMED, result.get(1).status());

        verify(orderRepository).findAllByOrderByCreatedAtDesc();
        verify(orderItemRepository).findAllByOrder_Id(1L);
        verify(orderItemRepository).findAllByOrder_Id(2L);
    }

    @Test
    void getAllOrders_shouldReturnEmptyListWhenNoOrdersExist() {
        when(orderRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of());

        var result = orderService.getAllOrders();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(orderRepository).findAllByOrderByCreatedAtDesc();
        verifyNoInteractions(orderItemRepository);
    }
}
