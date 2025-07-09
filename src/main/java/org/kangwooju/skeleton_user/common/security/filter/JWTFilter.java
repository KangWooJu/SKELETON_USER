package org.kangwooju.skeleton_user.common.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.kangwooju.skeleton_user.common.security.auth.UserDetailsImpl;
import org.kangwooju.skeleton_user.common.security.dto.response.ExpiredJwtResponse;
import org.kangwooju.skeleton_user.common.security.dto.response.InvalidTokenCategoryResponse;
import org.kangwooju.skeleton_user.common.security.util.JwtUtil;
import org.kangwooju.skeleton_user.domain.user.entity.User;
import org.kangwooju.skeleton_user.domain.user.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.LocalDateTime;



@Slf4j
public class JWTFilter extends OncePerRequestFilter {


    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    private final AntPathMatcher antPathMatcher;

    public JWTFilter(JwtUtil jwtUtil,
                     UserRepository userRepository,
                     ObjectMapper objectMapper,
                     AntPathMatcher antPathMatcher){
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.antPathMatcher = antPathMatcher;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        return antPathMatcher.match("/login",path)||
                antPathMatcher.match("/refresh",path)||
                antPathMatcher.match("/user/check-nickname", path) ||
                antPathMatcher.match("/user/check-username", path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String accessToken = extractToken(request);
        // 토큰이 비었는지 검사
        if(accessToken == null){
            log.info(" AccessToken NULL "+" [ Time : " + LocalDateTime.now() + " ]");
            filterChain.doFilter(request,response); // 다음 필터 진행
            return;
        }

        log.info("Authorization NOW : " + accessToken + " [ Time : " + LocalDateTime.now() + " ]");

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try{
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e){
            handleExpiredJwt(response);
            return;
        }

        String category = jwtUtil.getCategory(accessToken);

        if(!category.equals("access")){
            log.info("Token Invalid Category " + " [ Time : " + LocalDateTime.now() + " ]");
            handleInvalidTokenCategory(response,category);
            return;
        }

        log.info("잘된다눙");

        // makeToken()메소드를 사용해서 토큰 생성 후
        SecurityContextHolder.getContext().setAuthentication(makeToken(accessToken));
        filterChain.doFilter(request,response); // 다음 필터 진행
    }

    private String extractToken(HttpServletRequest request){
        String header = request.getHeader("Authorization");
        if(header == null || !header.startsWith("Bearer ")){
            return null;
        }
        return header.substring(7);
    }

    private Authentication makeToken(String token){
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

    private void handleExpiredJwt(HttpServletResponse response) throws IOException{

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
            ExpiredJwtResponse expiredJwtResponse =
                    new ExpiredJwtResponse("Expired","JwtToken Has Expired" +
                            " [ Time : " +
                            LocalDateTime.now() +
                            " ]");

            String json = objectMapper.writeValueAsString(expiredJwtResponse);
            //response body
            response.getWriter().write(json);
            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 상태코드 추가
            response.setContentType("application/json;charset=UTF-8"); // Content-Type 지정

    }

    private void handleInvalidTokenCategory(HttpServletResponse response,
                                            String category) throws IOException{

        InvalidTokenCategoryResponse invalidTokenCategoryResponse =
                new InvalidTokenCategoryResponse(category,"Invalid Token Category Found" +
                        " [ Time : " +
                        LocalDateTime.now() +
                        " ]");

        String json = objectMapper.writeValueAsString(invalidTokenCategoryResponse);
        response.getWriter().write(json);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
    }


}
