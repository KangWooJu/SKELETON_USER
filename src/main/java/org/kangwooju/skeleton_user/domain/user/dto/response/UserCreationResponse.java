package org.kangwooju.skeleton_user.domain.user.dto.response;

import java.time.LocalDateTime;

public record UserCreationResponse(String username,
                                   String nickname,
                                   LocalDateTime creationDate) {


}
