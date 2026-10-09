package entity;


import DTO.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name="users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length=30)
    private String name;

    @Column(nullable = false,unique = true,length = 30)
    private String email;

    @Column(nullable = false,length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 10)
    private Role role;

    @Column(nullable = false,updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate(){
        if(createdAt==null){
            createdAt=Instant.now();
        }
    }
}
