package com.iharshashkou.internetstore.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = @Index(
                name = "idx_refresh_token_jti",
                columnList = "token_id",
                unique = true
        )
)
@Getter
@Setter
@NoArgsConstructor
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_id", nullable = false, unique = true, length = 36)
    private String tokenId;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column
    private Instant revokedAt;

    public RefreshTokenEntity(
            String tokenId,
            String email,
            Instant expiresAt
    ) {
        this.tokenId = tokenId;
        this.email = email;
        this.expiresAt = expiresAt;
    }

    public boolean isActive() {
        return revokedAt == null && expiresAt.isAfter(Instant.now());
    }
}