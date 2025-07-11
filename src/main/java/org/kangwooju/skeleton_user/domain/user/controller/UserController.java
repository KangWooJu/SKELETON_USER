package org.kangwooju.skeleton_user.domain.user.controller;

import jakarta.validation.Valid;
import org.kangwooju.skeleton_user.common.dto.response.ApiSuccessResponse;
import org.kangwooju.skeleton_user.domain.user.dto.request.UserChangeNicknameRequest;
import org.kangwooju.skeleton_user.domain.user.dto.request.UserCreationRequest;
import org.kangwooju.skeleton_user.domain.user.dto.response.UserCreationResponse;
import org.kangwooju.skeleton_user.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 회원 생성하기 : 중복 검사 API 실행 후 request 필요
    @PostMapping("/create")
    public ResponseEntity<UserCreationResponse> createUser
    (@Valid @RequestBody UserCreationRequest request){

        UserCreationResponse response = userService.userCreate(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);

    }

    // 유저의 nickname 중복 API
    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNicknameDuplication
    (@RequestParam String nickname){

        userService.testDuplicationNickname(nickname);
        return ResponseEntity
                .ok(new ApiSuccessResponse("사용가능한 닉네임 입니다.",
                        LocalDateTime.now().toString()));
    }

    // 유저의 username 중복 API
    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsernameDuplication
    (@RequestParam String username){

        userService.testDuplicationUsername(username);
        return ResponseEntity
                .ok(new ApiSuccessResponse("사용가능한 아이디 입니다.",
                        LocalDateTime.now().toString()));
    }

    // 유저의 nickname을 변경하는 API
    @PatchMapping("/update/nickname")
    public ResponseEntity<ApiSuccessResponse> updateNickname
            (@Valid
             @RequestParam String nickname){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println(username);

        ApiSuccessResponse response = userService.updateNickname(nickname);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


}