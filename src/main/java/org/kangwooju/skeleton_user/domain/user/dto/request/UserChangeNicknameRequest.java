package org.kangwooju.skeleton_user.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserChangeNicknameRequest(

        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9]*$")
        @Size(max = 20)
        String nickname) {

}
