package org.kangwooju.skeleton_user.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException{

    private ErrorCode errorCode;

}
