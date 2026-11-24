package com.iharshashkou.internetstore.repository.order;

import com.iharshashkou.internetstore.DTO.user.Role;
import com.iharshashkou.internetstore.entity.Order;
import com.iharshashkou.internetstore.entity.User;
import com.iharshashkou.internetstore.repository.BaseRepositoryTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderRepositoryTest extends BaseRepositoryTest {

    @Test
    void findAllByUserIdOrderByCreatedAtDesc_shouldReturnUserOrdersNewestFirst() {
        User user1 = userRepository.save(createUser("Order User 1", "order1@example.com"));
        User user2 = userRepository.save(createUser("Order User 2", "order2@example.com"));

        Order oldOrder = orderRepository.save(createOrder(user1, Instant.parse("2026-01-01T10:00:00Z")));
        Order newOrder = orderRepository.save(createOrder(user1, Instant.parse("2026-01-03T10:00:00Z")));

        orderRepository.save(createOrder(user2, Instant.parse("2026-01-02T10:00:00Z")));

        List<Order> result = orderRepository.findAllByUser_IdOrderByCreatedAtDesc(user1.getId());

        assertEquals(2, result.size());
        assertEquals(newOrder.getId(), result.get(0).getId());
        assertEquals(oldOrder.getId(), result.get(1).getId());
        assertTrue(result.stream()
                .allMatch(order -> order.getUser().getId().equals(user1.getId())));
    }

    @Test
    void findByIdAndUserId_shouldReturnOrderOnlyForCorrectUser() {
        User user1 = userRepository.save(createUser("Owner", "owner@example.com"));
        User user2 = userRepository.save(createUser("Other User", "other@example.com"));

        Order order = orderRepository.save(
                createOrder(user1, Instant.parse("2026-02-01T10:00:00Z"))
        );

        var result = orderRepository.findByIdAndUser_Id(
                order.getId(),
                user1.getId()
        );

        assertTrue(result.isPresent());
        assertEquals(order.getId(), result.get().getId());

        assertTrue(orderRepository.findByIdAndUser_Id(
                order.getId(),
                user2.getId()
        ).isEmpty());
    }

    @Test
    void findAllByOrderByCreatedAtDesc_shouldReturnAllOrdersNewestFirst() {
        User user1 = userRepository.save(createUser("Admin Test User 1", "admin-test1@example.com"));
        User user2 = userRepository.save(createUser("Admin Test User 2", "admin-test2@example.com"));

        Order oldOrder = orderRepository.save(
                createOrder(user1, Instant.parse("2026-03-01T10:00:00Z"))
        );

        Order newOrder = orderRepository.save(
                createOrder(user2, Instant.parse("2026-03-03T10:00:00Z"))
        );

        Order middleOrder = orderRepository.save(
                createOrder(user1, Instant.parse("2026-03-02T10:00:00Z"))
        );

        List<Order> result = orderRepository.findAllByOrderByCreatedAtDesc();

        assertEquals(3, result.size());
        assertEquals(newOrder.getId(), result.get(0).getId());
        assertEquals(middleOrder.getId(), result.get(1).getId());
        assertEquals(oldOrder.getId(), result.get(2).getId());
    }

    private User createUser(String name, String email) {
        return User.builder()
                .name(name)
                .email(email)
                .password("encodedPassword")
                .role(Role.USER)
                .build();
    }

    private Order createOrder(User user, Instant createdAt) {
        return Order.builder()
                .user(user)
                .recipientName("Test Recipient")
                .phone("+1234567890")
                .deliveryAddress("Test address")
                .totalPrice(new BigDecimal("100.00"))
                .createdAt(createdAt)
                .build();
    }
}
