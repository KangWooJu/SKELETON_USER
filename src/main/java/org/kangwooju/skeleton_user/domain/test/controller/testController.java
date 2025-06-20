package org.kangwooju.skeleton_user.domain.test.controller;

import org.kangwooju.skeleton_user.common.utils.JsonResponseUtils;
import org.kangwooju.skeleton_user.domain.test.dto.request.TestRequest;
import org.kangwooju.skeleton_user.domain.test.dto.response.TestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class testController {

    @GetMapping("/test")
    public ResponseEntity<TestResponse> testForMvc(@RequestBody TestRequest testRequest){

        TestResponse testResponse = new TestResponse("JWT 방식 테스트 완료", LocalDateTime.now().toString());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(testResponse);
    }
}
