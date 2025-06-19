package org.kangwooju.skeleton_user.common.security.filter.response;

public record FilterSuccessResponse(boolean success,
                                 String method,
                                 String message,
                                 String timestamp) {
}
