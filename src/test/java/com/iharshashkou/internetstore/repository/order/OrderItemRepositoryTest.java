package com.iharshashkou.internetstore.repository.order;

import com.iharshashkou.internetstore.DTO.user.Role;
import com.iharshashkou.internetstore.entity.Order;
import com.iharshashkou.internetstore.entity.OrderItem;
import com.iharshashkou.internetstore.entity.Product;
import com.iharshashkou.internetstore.entity.User;
import com.iharshashkou.internetstore.repository.BaseRepositoryTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderItemRepositoryTest extends BaseRepositoryTest {

    @Test
    void findAllByOrderId_shouldReturnAllItemsForSpecifiedOrder() {
        User user = userRepository.save(
                User.builder()
                        .name("Order Items User")
                        .email("order-items@example.com")
                        .password("encodedPassword")
                        .role(Role.USER)
                        .build()
        );

        Order order1 = orderRepository.save(createOrder(user));
        Order order2 = orderRepository.save(createOrder(user));

        Product product1 = productRepository.save(createProduct("Oak Table"));
        Product product2 = productRepository.save(createProduct("Epoxy Table"));

        orderItemRepository.saveAll(List.of(
                createOrderItem(order1, product1, "Oak Table"),
                createOrderItem(order1, product2, "Epoxy Table"),
                createOrderItem(order2, product1, "Oak Table")
        ));

        List<OrderItem> result = orderItemRepository.findAllByOrder_Id(order1.getId());

        assertEquals(2, result.size());
        assertTrue(result.stream()
                .allMatch(item -> item.getOrder().getId().equals(order1.getId())));
    }

    private Order createOrder(User user) {
        return Order.builder()
                .user(user)
                .recipientName("Test Recipient")
                .phone("+1234567890")
                .deliveryAddress("Test address")
                .totalPrice(new BigDecimal("200.00"))
                .build();
    }

    private Product createProduct(String name) {
        return Product.builder()
                .name(name)
                .description("Test description")
                .price(new BigDecimal("100.00"))
                .stockQuantity(10)
                .build();
    }

    private OrderItem createOrderItem(Order order, Product product, String productName) {
        return OrderItem.builder()
                .order(order)
                .product(product)
                .productName(productName)
                .price(new BigDecimal("100.00"))
                .quantity(2)
                .subtotal(new BigDecimal("200.00"))
                .build();
    }
}
