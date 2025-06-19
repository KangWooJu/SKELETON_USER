package org.kangwooju.skeleton_user.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {


    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 사용자를 찾을 수 없습니다."),
    USER_USERNAME_DUPLICATED(HttpStatus.CONFLICT,"해당 username은 이미 존재합니다."),
    USER_NICKNAME_DUPLICATED(HttpStatus.CONFLICT,"해당 nickname은 이미 존재합니다.");


    private final HttpStatus httpStatus;
    private final String message;
}
