package org.kangwooju.skeleton_user.common.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.kangwooju.skeleton_user.common.exception.CustomException;
import org.kangwooju.skeleton_user.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@Builder
@Getter
public class ApiErrorResponse {

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public ApiErrorResponse(ErrorCode errorCode){
        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.name();
        this.message = errorCode.getMessage();
    }

    public static ResponseEntity<ApiErrorResponse> error(CustomException e){
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus()) // status 설정
                .body(ApiErrorResponse // body 설정
                        .builder()
                        .httpStatus(e.getErrorCode().getHttpStatus())
                        .code(e.getErrorCode().name())
                        .message(e.getErrorCode().getMessage())
                        .build());
    }
}
