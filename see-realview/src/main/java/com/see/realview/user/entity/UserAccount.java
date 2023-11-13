package com.see.realview.user.entity;

import com.see.realview.user.entity.constant.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_account_tb",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id", "email", "is_active"})
        },
        indexes = {
                @Index(name = "user_email_index", columnList = "email")
        })
@SQLDelete(sql = "UPDATE user_account_tb SET is_active = false WHERE id = ?")
@Where(clause = "is_active = true")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", length = 30, nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_active")
    private Boolean isActive;

    @Builder
    public UserAccount(Long id, String name, String email, String password, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = Role.USER;
        this.createdAt = (createdAt == null? LocalDateTime.now() : createdAt);
        this.isActive = true;
    }
}
