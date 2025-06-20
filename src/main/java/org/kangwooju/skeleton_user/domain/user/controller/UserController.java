package org.kangwooju.skeleton_user.domain.user.controller;

import org.kangwooju.skeleton_user.common.dto.response.ApiSuccessResponse;
import org.kangwooju.skeleton_user.domain.user.dto.request.UserCreationRequest;
import org.kangwooju.skeleton_user.domain.user.dto.response.UserCreationResponse;
import org.kangwooju.skeleton_user.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    (@RequestBody UserCreationRequest request){

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

}