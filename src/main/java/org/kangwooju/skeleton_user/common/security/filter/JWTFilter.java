package org.kangwooju.skeleton_user.common.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.kangwooju.skeleton_user.common.security.auth.UserDetailsImpl;
import org.kangwooju.skeleton_user.common.security.util.JwtUtil;
import org.kangwooju.skeleton_user.domain.user.entity.User;
import org.kangwooju.skeleton_user.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Predicate;


@Slf4j
public class JWTFilter extends OncePerRequestFilter {


    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JWTFilter(JwtUtil jwtUtil,UserRepository userRepository){
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String accessToken = extractToken(request);
        // 토큰이 비었는지 검사
        if(accessToken == null){
            log.info(" AccessToken NULL "+" [ Time : " + LocalDateTime.now() + " ]");
            filterChain.doFilter(request,response);
            return;
        }

        log.info("Authorization NOW : " + accessToken + " [ Time : " + LocalDateTime.now() + " ]");
        String token = accessToken.split(" ")[1];

        // 토큰 만료여부 확인
        if(jwtUtil.isExpired(token)){
            log.info(" Token EXPIRED : " + accessToken + " [ Time : " + LocalDateTime.now() + " ]");
            filterChain.doFilter(request,response);
            return;
        }

        // makeToken()메소드를 사용해서 토큰 생성 후
        SecurityContextHolder.getContext().setAuthentication(makeToken(token));
        filterChain.doFilter(request,response);
    }

    public String extractToken(HttpServletRequest request){
        String header = request.getHeader("Authorization");
        if(header == null || !header.startsWith("Bearer ")){
            return null;
        }
        return header.substring(7);
    }

    public Authentication makeToken(String token){
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> {
                    log.error("해당 유저 정보를 찾을 수 없습니다. username : " +
                            username +
                            " [ Time : " + LocalDateTime.now() + " ]");
                    throw new UsernameNotFoundException("해당 유저정보를 찾을 수 없습니다.");
                });


        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        // 토큰 생성 ( LoginFIlter 전 인증 생성 -> 이후에 LoginFilter에서는 username,password만 넣어 인증 요청 !! 둘이 파라미터 다름  )
        Authentication authentication =
                new UsernamePasswordAuthenticationToken
                        (userDetails,null,userDetails.getAuthorities());

        return authentication;
    }
}
