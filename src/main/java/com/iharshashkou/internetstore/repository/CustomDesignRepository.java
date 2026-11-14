package com.iharshashkou.internetstore.repository;

import com.iharshashkou.internetstore.entity.customDesignEntity.CustomDesign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomDesignRepository extends JpaRepository<CustomDesign, Long> {

    // Проекты конкретного пользователя
    List<CustomDesign> findAllByUser_IdOrderByCreatedAtDesc(Long userId);

    // Поиск проекта с проверкой владельца
    Optional<CustomDesign> findByIdAndUser_Id(Long designId, Long userId);

    // Все проекты для администратора
    List<CustomDesign> findAllByOrderByCreatedAtDesc();

    // Поиск гостевого проекта по хешу токена
    Optional<CustomDesign> findByGuestTokenHash(String guestTokenHash);

    //Проверка ID проекта и секретного токена для отсутствия возможности отправить на рассмотрение чужой проект, зная только его ID
    Optional<CustomDesign> findByIdAndGuestTokenHash(
            Long designId,
            String guestTokenHash
    );
}