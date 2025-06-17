package org.kangwooju.skeleton_user.common.dto.response;

public record ApiSuccessResponse(boolean success,
                                 String method,
                                 String message,
                                 String timestamp) {
}
