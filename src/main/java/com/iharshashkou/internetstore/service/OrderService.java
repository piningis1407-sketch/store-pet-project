package com.iharshashkou.internetstore.service;

import com.iharshashkou.internetstore.DTO.order.CreateOrderRequest;
import com.iharshashkou.internetstore.DTO.order.OrderItemResponse;
import com.iharshashkou.internetstore.DTO.order.OrderResponse;
import com.iharshashkou.internetstore.entity.*;
import com.iharshashkou.internetstore.exception.CartConflictException;
import com.iharshashkou.internetstore.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderResponse createOrder(
            Long userId,
            CreateOrderRequest request
    ) {
        // Блокируем корзину пользователя.
        Cart cart = cartRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() ->
                        new CartConflictException("Корзина пуста")
                );

        List<CartItem> cartItems =
                cartItemRepository.findAllByCart_Id(cart.getId());

        if (cartItems.isEmpty()) {
            throw new CartConflictException("Корзина пуста");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Пользователь не найден")
                );

        Order order = Order.builder()
                .user(user)
                .recipientName(request.recipientName().trim())
                .phone(request.phone().trim())
                .deliveryAddress(request.deliveryAddress().trim())
                .status(OrderStatus.NEW)
                .totalPrice(BigDecimal.ZERO)
                .build();

        order = orderRepository.save(order);

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {

            // Блокируем товар на время оформления заказа.
            Product product = productRepository
                    .findByIdForUpdate(cartItem.getProduct().getId())
                    .orElseThrow(() ->
                            new EntityNotFoundException("Товар не найден")
                    );

            int quantity = cartItem.getQuantity();

            if (!product.isActive()) {
                throw new CartConflictException(
                        "Товар " + product.getName() + " недоступен"
                );
            }

            if (product.getStockQuantity() < quantity) {
                throw new CartConflictException(
                        "Недостаточное количество товара: "
                                + product.getName()
                );
            }

            BigDecimal price = product.getPrice();

            BigDecimal subtotal = price.multiply(
                    BigDecimal.valueOf(quantity)
            );

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .price(price)
                    .quantity(quantity)
                    .subtotal(subtotal)
                    .build();

            orderItemRepository.save(orderItem);

            totalPrice = totalPrice.add(subtotal);

            // Списываем товар со склада.
            product.setStockQuantity(
                    product.getStockQuantity() - quantity
            );
        }

        order.setTotalPrice(totalPrice);

        // Очищаем корзину только после успешной проверки всех товаров.
        cartItemRepository.deleteAllByCart_Id(cart.getId());

        orderRepository.save(order);

        return getOrderResponse(order);
    }

    private OrderResponse getOrderResponse(Order order) {

        List<OrderItemResponse> items = orderItemRepository
                .findAllByOrder_Id(order.getId())
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProductName(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getSubtotal()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getRecipientName(),
                order.getPhone(),
                order.getDeliveryAddress(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                items
        );
    }

    public List<OrderResponse> getUserOrders(Long userId) {
        return orderRepository
                .findAllByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::getOrderResponse)
                .toList();
    }

    public OrderResponse getUserOrder(Long userId, Long orderId) {
        Order order = orderRepository
                .findByIdAndUser_Id(orderId, userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Заказ не найден")
                );

        return getOrderResponse(order);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::getOrderResponse)
                .toList();
    }

    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Заказ не найден")
                );

        OrderStatus currentStatus = order.getStatus();

        if (currentStatus == newStatus) {
            return getOrderResponse(order);
        }

        boolean allowed = switch (currentStatus) {
            case NEW -> newStatus == OrderStatus.CONFIRMED
                    || newStatus == OrderStatus.CANCELLED;
            case CONFIRMED -> newStatus == OrderStatus.PROCESSING
                    || newStatus == OrderStatus.CANCELLED;
            case PROCESSING -> newStatus == OrderStatus.SHIPPED
                    || newStatus == OrderStatus.CANCELLED;
            case SHIPPED -> newStatus == OrderStatus.COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };

        if (!allowed) {
            throw new IllegalStateException(
                    "Недопустимый переход статуса заказа: "
                            + currentStatus + " -> " + newStatus
            );
        }

        if (newStatus == OrderStatus.CANCELLED) {
            List<OrderItem> items =
                    orderItemRepository.findAllByOrder_Id(orderId);

            for (OrderItem item : items) {
                Product product = productRepository
                        .findByIdForUpdate(item.getProduct().getId())
                        .orElseThrow(() ->
                                new EntityNotFoundException("Товар не найден")
                        );

                product.setStockQuantity(
                        product.getStockQuantity() + item.getQuantity()
                );
            }
        }

        order.setStatus(newStatus);

        return getOrderResponse(orderRepository.save(order));
    }
}