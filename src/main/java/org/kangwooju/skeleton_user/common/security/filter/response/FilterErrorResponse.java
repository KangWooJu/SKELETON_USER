package org.kangwooju.skeleton_user.common.security.filter.response;

public record FilterErrorResponse(boolean error,
                               String method,
                               String message,
                               String timestamp) {
}
