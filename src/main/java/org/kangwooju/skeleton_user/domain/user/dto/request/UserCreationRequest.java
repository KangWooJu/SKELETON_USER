package org.kangwooju.skeleton_user.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record UserCreationRequest(String username,
                                  String password,
                                  @NotBlank
                                  @Pattern(regexp = "^[a-zA-Z0-9]*$")
                                  @Size(max = 20)
                                  String nickname) {

}
