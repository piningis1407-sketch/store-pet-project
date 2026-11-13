package com.iharshashkou.internetstore.controller;

import com.iharshashkou.internetstore.DTO.order.CreateOrderRequest;
import com.iharshashkou.internetstore.DTO.order.OrderResponse;
import com.iharshashkou.internetstore.DTO.order.UpdateOrderStatusRequest;
import com.iharshashkou.internetstore.repository.UserRepository;
import com.iharshashkou.internetstore.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUserOrders(
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);

        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getUserOrder(
            @PathVariable Long orderId,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);

        return ResponseEntity.ok(
                orderService.getUserOrder(userId, orderId)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        return ResponseEntity.ok(
                orderService.updateOrderStatus(orderId, request.status())
        );
    }

    private Long getUserId(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Пользователь не найден"
                ))
                .getId();
    }
}