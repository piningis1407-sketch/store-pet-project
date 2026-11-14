package com.iharshashkou.internetstore.entity.customDesignEntity;
import com.iharshashkou.internetstore.entity.Category;
import com.iharshashkou.internetstore.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "custom_designs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomDesign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Пользователь может отсутствовать, если проект создаёт гость
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Используем существующие категории магазина
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WoodType woodType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DesignShape shape;

    @Column(nullable = false)
    private boolean epoxyEnabled;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EpoxyColor epoxyColor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DesignBackground background;

    // Размеры в сантиметрах
    private Integer lengthCm;

    private Integer widthCm;

    private Integer heightCm;

    private Integer diameterCm;

    @Column(length = 2000)
    private String comment;

    @Column(length = 2000)
    private String adminComment;

    @Column(length = 1000)
    private String imageUrl;

    // Автоматическая предварительная оценка
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal estimatedPrice;

    // Итоговая цена после проверки администратором
    @Column(precision = 12, scale = 2)
    private BigDecimal finalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private CustomDesignStatus status = CustomDesignStatus.GENERATED;

    // Хеш токена для доступа гостя к проекту
    @Column(unique = true, length = 64)
    private String guestTokenHash;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
