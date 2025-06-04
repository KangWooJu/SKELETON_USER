package org.kangwooju.skeleton_user.common.security.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.kangwooju.skeleton_user.common.security.dto.response.ReissueResponse;
import org.kangwooju.skeleton_user.common.security.util.JwtUtil;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReissueService {

    private final JwtUtil jwtUtil;

    public ReissueService(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    private String findCookie(HttpServletRequest request){

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh".equals(cookie.getName())) {
                    refresh = cookie.getValue();
                }
            }
        }

        return refresh;

    }

    public ReissueResponse refreshCookies(HttpServletRequest request){

        String refresh = findCookie(request);
        if(refresh == null){
            return new ReissueResponse("Refresh NULL","Refresh NULL " +
                    "[ Time : " +LocalDateTime.now() +
                    " ]",null,null);
        }

        try{
            jwtUtil.isExpired(refresh);

        }catch (ExpiredJwtException e){
            return new ReissueResponse("Refresh EXPIRED","Refresh EXPIRED " +
                    "[ Time : " + LocalDateTime.now() +
                    " ]",null,null);
        }

        return new ReissueResponse("Refresh EXISTS","Refresh EXISTS " +
                "[ Time : " + LocalDateTime.now() +
                " ]",resetAccessToken(request),reissueRefresh(request));
    }

    private String resetAccessToken(HttpServletRequest request){

        String refresh = findCookie(request);

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);
        return jwtUtil.createJwt("access",username,role,600000L);

    }

    public String reissueRefresh(HttpServletRequest request){

        String refresh = findCookie(request);
        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);
        return jwtUtil.createJwt("refresh",username,role,8640000L);
    }

    public Cookie createCookie(String key,String value){

        Cookie cookie = new Cookie(key,value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(true);

        return cookie;
    }
}
