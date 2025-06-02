package org.kangwooju.skeleton_user.domain.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.kangwooju.skeleton_user.domain.user.dto.request.UserCreationRequest;
import org.kangwooju.skeleton_user.domain.user.dto.response.UserCreationResponse;
import org.kangwooju.skeleton_user.domain.user.entity.Role;
import org.kangwooju.skeleton_user.domain.user.entity.User;
import org.kangwooju.skeleton_user.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.function.BiPredicate;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public UserCreationResponse Usercreate
            (UserCreationRequest userCreationRequest){

        // 중복여부 체크 메소드 먼저 실행

        User user = User.builder()
                .username(userCreationRequest.username())
                .password(bCryptPasswordEncoder.encode(userCreationRequest.password()))
                .nickname(userCreationRequest.nickname())
                .createDate(LocalDateTime.now())
                .role(Role.NORMAL) // 일반 유저의 경우 Role.NORMAL 타입으로 설정
                .build();


        UserCreationResponse userCreationResponse
                = new UserCreationResponse(userCreationRequest.username(),
                userCreationRequest.nickname(),
                userCreationRequest.creationDate());

        userRepository.save(user);
        return userCreationResponse;

    }

    // 회원가입 시 Username 중복을 테스트 하는 로직
    public boolean TestDuplicationUsername(String username){

        User findUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username중복"));
        // 커스텀 예외처리로 변경 예정

        BiPredicate<String,String> matches = String::equals;
        boolean result = matches.test(findUser.getUsername(),username);

        return result;

    }

    // 회원가입 시 Nickname 중복을 테스트 하는 로직
    public boolean TestDuplicationNickname(String nickname){

        User findUser = userRepository.findByNickname(nickname)
                .orElseThrow(()-> new RuntimeException("nickname중복"));
        // 커스텀 예외처리로 변경 예정

        BiPredicate<String,String> matches = String::equals;
        boolean result = matches.test(findUser.getNickname(),nickname);

        return result;
    }






}
