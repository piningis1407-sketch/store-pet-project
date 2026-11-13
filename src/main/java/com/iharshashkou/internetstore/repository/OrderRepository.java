package com.iharshashkou.internetstore.repository;

import com.iharshashkou.internetstore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // История заказов конкретного пользователя
    List<Order> findAllByUser_IdOrderByCreatedAtDesc(Long userId);

    // Поиск заказа с проверкой владельца
    Optional<Order> findByIdAndUser_Id(Long orderId, Long userId);

    // Все заказы для администратора
    List<Order> findAllByOrderByCreatedAtDesc();
}
