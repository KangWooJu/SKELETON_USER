package org.kangwooju.skeleton_user.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // 시큐리티 필터 체인 설정
    @Bean
    public SecurityFilterChain SecurityfilterChain(HttpSecurity httpSecurity)
            throws Exception {

        httpSecurity
                .csrf((auth)->auth.disable());

        httpSecurity
                .formLogin((auth)->auth.disable());

        httpSecurity
                .httpBasic((auth)->auth.disable());

        // Http 주소허용 여부 설정 -> Default
        httpSecurity
                .authorizeHttpRequests((auth)->auth
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/admin","/").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        return httpSecurity.build();
    }
}
