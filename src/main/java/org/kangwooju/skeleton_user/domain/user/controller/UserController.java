package org.kangwooju.skeleton_user.domain.user.controller;

import org.kangwooju.skeleton_user.domain.user.dto.request.UserCreationRequest;
import org.kangwooju.skeleton_user.domain.user.dto.request.UserDuplicationTestNickname;
import org.kangwooju.skeleton_user.domain.user.dto.request.UserDuplicationTestUsername;
import org.kangwooju.skeleton_user.domain.user.dto.response.UserCreationResponse;
import org.kangwooju.skeleton_user.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    // 회원 생성하기 : 중복 검사 API 실행 후 request 필요
    @PostMapping("/create")
    public ResponseEntity<UserCreationResponse> createUser
    (@RequestBody UserCreationRequest request){

        UserCreationResponse response = userService.Usercreate(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);

    }

    // 유저의 nickname 중복 API
    @GetMapping("/create/test-nicnkame")
    public ResponseEntity<?> testNickname
    (@RequestBody UserDuplicationTestNickname request){

        userService.TestDuplicationNickname(request.nickname());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("중복된 닉네임입니다.");
    }

    // 유저의 username 중복 API
    @GetMapping("/create/test-username")
    public ResponseEntity<?> testUsername
    (@RequestBody UserDuplicationTestUsername request){

        userService.TestDuplicationUsername(request.username());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("중복된 아이디 입니다.");
    }
}
