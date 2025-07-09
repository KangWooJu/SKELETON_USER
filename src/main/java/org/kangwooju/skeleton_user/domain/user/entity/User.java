package org.kangwooju.skeleton_user.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.kangwooju.skeleton_user.domain.user.vo.Nickname;

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

    @Column(name="user_username",nullable = false,unique = true)
    private String username;

    @Column(name="user_password",nullable = false)
    private String password;

    @Embedded
    private Nickname nickname;

    @Column(name="user_createDate",nullable = false)
    private LocalDateTime createDate;

    @Enumerated(EnumType.STRING)
    @Column(name="user_level",nullable = false)
    private Role role;

    public void updateNickname(Nickname nickname){
        this.nickname = nickname;
    }
}

