package org.kangwooju.skeleton_user.domain.user.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Nickname {

    @Column(name = "user_nickname",nullable = false,length = 50,unique = true)
    private String nickname;

    protected Nickname(){}

    @JsonCreator
    public Nickname(@JsonProperty("nickname") String nickname){

        if (nickname == null || nickname.isBlank())
            throw new IllegalArgumentException("닉네임은 비어 있을 수 없습니다.");
        if (nickname.length() > 50)
            throw new IllegalArgumentException("닉네임은 20자를 넘을 수 없습니다.");

        this.nickname = nickname;
    }
}
