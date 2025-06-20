package org.kangwooju.skeleton_user.common.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.kangwooju.skeleton_user.common.security.dto.response.ReissueResponse;
import org.kangwooju.skeleton_user.common.security.service.ReissueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    @Autowired
    private ReissueService reissueService;


    @PostMapping("/refresh")
    public ResponseEntity<ReissueResponse> reissue(HttpServletRequest request,
                                                   HttpServletResponse response){

        ReissueResponse reissueResponse = reissueService.refreshCookies(request);

        return switch (reissueResponse.status()) {
            case "REFRESH_EXISTS" -> {
                response.setHeader("access", reissueResponse.AccessToken());
                response.addCookie(reissueService.createCookie("refresh",reissueService.reissueRefresh(request)));
                yield new ResponseEntity<>(reissueResponse, HttpStatus.OK);
            }
            default -> new ResponseEntity<>(reissueResponse, HttpStatus.BAD_REQUEST);
        };
    }
}
