package org.kangwooju.skeleton_user.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.kangwooju.skeleton_user.common.security.filter.JWTFilter;
import org.kangwooju.skeleton_user.common.security.filter.JwtLogoutFilter;
import org.kangwooju.skeleton_user.common.security.filter.LoginFilter;
import org.kangwooju.skeleton_user.common.security.repository.RefreshRepository;
import org.kangwooju.skeleton_user.common.security.service.ReissueService;
import org.kangwooju.skeleton_user.common.security.util.JwtUtil;
import org.kangwooju.skeleton_user.domain.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {


    private final AuthenticationConfiguration authenticationConfiguration;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final ReissueService reissueService;
    private final AntPathMatcher antPathMatcher;


    @Bean
    public JWTFilter jwtFilter(){
        return new JWTFilter(jwtUtil,
                userRepository,
                objectMapper,
                antPathMatcher);
    }

    // LoginFilter를 Config에서 bean으로 등록
    @Bean
    public LoginFilter loginFilter() throws Exception{
        return new LoginFilter(objectMapper,
                authenticationManager(authenticationConfiguration),
                jwtUtil,
                reissueService);
    }

    @Bean
    public JwtLogoutFilter jwtlogoutFilter() throws Exception{
        return new JwtLogoutFilter(jwtUtil,reissueService);
    }

    @Bean
    public AuthenticationManager authenticationManager
            (AuthenticationConfiguration authenticationConfiguration)
             throws Exception{

        return this.authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // 시큐리티 필터 체인 설정
    @Bean
    public SecurityFilterChain securityfilterChain(HttpSecurity httpSecurity,
                                                   LoginFilter loginFilter)
            throws Exception {

        httpSecurity
                .csrf((auth)->auth.disable());

        httpSecurity
                .formLogin((auth)->auth.disable());

        httpSecurity
                .httpBasic((auth)->auth.disable());

        httpSecurity
                .addFilterAt(loginFilter,
                        UsernamePasswordAuthenticationFilter.class); // 필터 순서 2 ( 로그인 )
        httpSecurity
                .addFilterBefore(jwtFilter(), LoginFilter.class); // 필터 순서 1 ( 로그인 )
        httpSecurity
                .addFilterBefore(jwtlogoutFilter(), LogoutFilter.class); // 필터순서 1 ( 로그아웃 )

        // CORS 설정
        httpSecurity
                .cors((corsCustomizer)->corsCustomizer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); // 허용 출처 지정
                        configuration.setAllowedMethods(Collections.singletonList("*")); // HTTP 메소드 지정
                        configuration.setAllowCredentials(true); // 인증 정보를 포함한 요청을 허용
                        configuration.setAllowedHeaders(Collections.singletonList("*")); // 클라이언트 요청 시 보낼 수 있는 헤더 지정
                        configuration.setMaxAge(3600L); // 브라우저 preflight 요청캐싱 시간 지정

                        return configuration;
                    }
                }));
        // 세션을 유지하지 않도록 하는 설정 -> STATELESS
        httpSecurity
                .sessionManagement((session)->session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Http 주소허용 여부 설정 -> Default
        httpSecurity
                .authorizeHttpRequests((auth)->auth
                        .requestMatchers("/login","/user/create","/user/check-nickname","/user/check-username").permitAll()
                        .requestMatchers("/refresh").permitAll()
                        .anyRequest().authenticated()
                );

        return httpSecurity.build();
    }
}
