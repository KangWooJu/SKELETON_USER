package org.kangwooju.skeleton_user.common.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.kangwooju.skeleton_user.common.security.auth.UserDetailsImpl;
import org.kangwooju.skeleton_user.common.security.dto.request.LoginRequest;
import org.kangwooju.skeleton_user.common.security.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

@Component
@AllArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {


    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;



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
                                                            loginRequest.passowrd());

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

        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        String username = userDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority grantedAuthority = iterator.next();

        String role = grantedAuthority.getAuthority();

        String token = jwtUtil.createJwt(username,role,60*60*10L);

        response.setHeader("Authorization", "Bearer "+token);
    }

    @Override
    protected void unsuccessfulAuthentication
            (HttpServletRequest request,
             HttpServletResponse response,
             AuthenticationException failed)
            throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");



    }
}