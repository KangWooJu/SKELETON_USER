package org.kangwooju.skeleton_user.domain.user.dto.request;

import java.time.LocalDateTime;

public record UserCreationRequest(String username,
                                  String password,
                                  String nickname,
                                  LocalDateTime creationDate) {

}
