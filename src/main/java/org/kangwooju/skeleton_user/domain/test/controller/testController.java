package org.kangwooju.skeleton_user.domain.test.controller;

import org.kangwooju.skeleton_user.domain.test.dto.response.TestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
public class testController {

    @PostMapping("/test")
    public ResponseEntity<TestResponse> testForMvc(@RequestParam String username){

        System.out.println(username);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username2 = auth.getName();
        System.out.println(username2);

        TestResponse testResponse = new TestResponse("JWT 방식 테스트 완료", LocalDateTime.now().toString());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(testResponse);
    }
}
