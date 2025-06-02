package org.kangwooju.skeleton_user.common.security.dto.response;

public record LoginFailedResponse(boolean success,
                                  String errorCode,
                                  String message) {
}
