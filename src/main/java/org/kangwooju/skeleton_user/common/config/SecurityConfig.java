package org.kangwooju.skeleton_user.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.kangwooju.skeleton_user.common.security.filter.JWTFilter;
import org.kangwooju.skeleton_user.common.security.filter.LoginFilter;
import org.kangwooju.skeleton_user.common.security.util.JwtUtil;
import org.kangwooju.skeleton_user.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {


    private final AuthenticationConfiguration authenticationConfiguration;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private final LoginFilter loginFilter;
    private final JWTFilter jwtFilter;
    private final UserRepository userRepository;

    @Bean
    public JWTFilter jwtFilter(JwtUtil jwtUtil,UserRepository userRepository){
        return new JWTFilter(jwtUtil,userRepository);
    }


    // LoginFilter를 Config에서 bean으로 등록
    @Bean
    public LoginFilter loginFilter(ObjectMapper objectMapper,
                                   AuthenticationConfiguration authenticationConfiguration,
                                   JwtUtil jwtUtil) throws Exception{
        return new LoginFilter(objectMapper,authenticationManager(authenticationConfiguration),jwtUtil);
    }

    @Bean
    public AuthenticationManager authenticationManager
            (AuthenticationConfiguration authenticationConfiguration)
             throws Exception{

        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // 시큐리티 필터 체인 설정
    @Bean
    public SecurityFilterChain SecurityfilterChain(HttpSecurity httpSecurity,
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
                        UsernamePasswordAuthenticationFilter.class); // 필터 순서 2
        httpSecurity
                .addFilterBefore(jwtFilter, LoginFilter.class); // 필터 순서 1
        // 세션을 유지하지 않도록 하는 설정 -> STATELESS
        httpSecurity
                .sessionManagement((session)->session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Http 주소허용 여부 설정 -> Default
        httpSecurity
                .authorizeHttpRequests((auth)->auth
                        .requestMatchers("/login", "/","/user/**").permitAll()
                        .anyRequest().authenticated()
                );

        return httpSecurity.build();
    }
}
