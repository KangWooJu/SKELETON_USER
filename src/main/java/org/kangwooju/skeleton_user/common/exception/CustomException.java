package org.kangwooju.skeleton_user.common.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomException extends RuntimeException{

    private final ErrorCode errorCode;
}
