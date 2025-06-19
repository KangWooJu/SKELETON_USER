package org.kangwooju.skeleton_user.common.exception;

import org.kangwooju.skeleton_user.common.dto.response.CustomErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomErrorResponse> handleCustomException(CustomException e){

        return CustomErrorResponse.error(e);
    }
}
