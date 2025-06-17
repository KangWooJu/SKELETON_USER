package org.kangwooju.skeleton_user.common.dto.response;

public record ApiErrorResponse(boolean error,
                               String method,
                               String message,
                               String timestamp) {
}
