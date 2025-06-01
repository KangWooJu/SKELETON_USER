package org.kangwooju.skeleton_user.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="user_username",nullable = false)
    private String username;

    @Column(name="user_password",nullable = false)
    private String password;

    @Column(name="user_nickname",nullable = false)
    private String nickname;

    @Column(name="user_createDate",nullable = false)
    private LocalDateTime createDate;

    @Enumerated(EnumType.ORDINAL)
    @Column(name="user_level",nullable = false)
    private Role role;
}

