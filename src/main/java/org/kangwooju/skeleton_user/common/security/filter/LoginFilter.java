package org.kangwooju.skeleton_user.common.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.kangwooju.skeleton_user.common.security.dto.request.LoginRequest;
import org.kangwooju.skeleton_user.common.security.dto.response.LoginFailedResponse;
import org.kangwooju.skeleton_user.common.security.service.ReissueService;
import org.kangwooju.skeleton_user.common.security.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;


@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private  final ReissueService reissueService;

    public LoginFilter(ObjectMapper objectMapper,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil,
                       ReissueService reissueService){
        this.objectMapper = objectMapper;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.reissueService = reissueService;
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication
            (HttpServletRequest request,
             HttpServletResponse response)
            throws AuthenticationException {

        try {

            // 스트림을 통해 JSON 형식으로 로그인 정보를 받아오는 로직
            InputStream inputStream = request.getInputStream();
            LoginRequest loginRequest =
                    objectMapper.readValue(inputStream, LoginRequest.class);

            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(loginRequest.username(),
                                                            loginRequest.password());

            return authenticationManager.authenticate(token);


        } catch (IOException e) {
            throw new AuthenticationServiceException("JSON 파싱 오류",e);
        }
    }

    // 로그인 성공 메소드
    @Override
    protected void successfulAuthentication
            (HttpServletRequest request,
             HttpServletResponse response,
             FilterChain chain,
             Authentication authResult)
            throws IOException, ServletException {

        String username = authResult.getName(); // Principal의 username을 반환

        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority grantedAuthority = iterator.next();

        String role = grantedAuthority.getAuthority();

        String access = jwtUtil.createJwt("access",username,role,600000L);
        String refresh = jwtUtil.createJwt("refresh",username,role,8640000L);

        reissueService.addRefresh(username,refresh,86400000L);

        response.setHeader("accessToken",access);
        response.addCookie(reissueService.createCookie("refreshToken",refresh));
        response.setStatus(HttpStatus.OK.value());
        log.info("[ 로그인 성공 ] TIME : {} , USER : {} ", LocalDateTime.now(),username);
    }

    @Override
    protected void unsuccessfulAuthentication
            (HttpServletRequest request,
             HttpServletResponse response,
             AuthenticationException failed)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        LoginFailedResponse loginFailedResponse =
                new LoginFailedResponse(false,
                                        "AUTH_FAILED", // CustomException생성시 리팩토링 예정
                                        "아이디 또는 비밀번호가 일치하지 않습니다.");

        String json = objectMapper.writeValueAsString(loginFailedResponse);

        response.getWriter().write(json); // 프론트에서 JSON 형식을 받아 사용할 수 있도록 전달
        response.getWriter().flush();
        response.flushBuffer();
    }
}