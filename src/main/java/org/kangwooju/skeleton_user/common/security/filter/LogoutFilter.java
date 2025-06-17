package org.kangwooju.skeleton_user.common.security.filter;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.kangwooju.skeleton_user.common.dto.response.ApiErrorResponse;
import org.kangwooju.skeleton_user.common.dto.response.ApiSuccessResponse;
import org.kangwooju.skeleton_user.common.security.repository.RefreshRepository;
import org.kangwooju.skeleton_user.common.security.service.ReissueService;
import org.kangwooju.skeleton_user.common.security.util.JwtUtil;
import org.kangwooju.skeleton_user.common.utils.JsonResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.function.Predicate;


@RequiredArgsConstructor
public class LogoutFilter extends OncePerRequestFilter {

    private final RefreshRepository refreshRepository;
    private final JwtUtil jwtUtil;
    private final ReissueService reissueService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // URI와 Method가 일치하는지 검사
        if(!checkInvalidLogout(request).error()){
            JsonResponseUtils.writeJsonResponse(HttpStatus.BAD_REQUEST,
                    response
                    ,checkInvalidLogout(request));
            filterChain.doFilter(request,response);
            return;
        }

        String refresh = reissueService.findCookie(request);

        // Refresh토큰에 오류가 있는지 검사
        if(!checkRefresh(refresh).error()){
            JsonResponseUtils.writeJsonResponse(HttpStatus.BAD_REQUEST,
                    response,
                    checkInvalidLogout(request));
        }

        // Refresh 삭제하기
        refreshRepository.deleteByRefresh(refresh);
        // Cookie를 빈 쿠키로 설정하기
        reissueService.zeroCookie(response);
        // 로그아웃 성공 Response반환
        JsonResponseUtils.writeJsonResponse(HttpStatus.OK,
                response,
                new ApiSuccessResponse(true,
                        "Method : /logout ",
                        "로그아웃에 성공하였습니다.",
                        LocalDateTime.now().toString())
                );
    }

    // 1st. URI와 Method를 검사
    private ApiErrorResponse checkInvalidLogout(HttpServletRequest request){

        Predicate<HttpServletRequest> logoutRequest =
                requestParam -> "/logout".equals(requestParam.getRequestURI())&&
                        "POST".equals(requestParam.getMethod());

        ApiErrorResponse apiErrorResponse =
                new ApiErrorResponse(false,
                        "Method : " + request.getMethod(),
                        "API의 URI 혹은 Method 타입이 불일치합니다.\n"
                                +"URI : " + request.getRequestURI() + "\n"
                                +"Method : " + request.getMethod() + "\n",
                        "Time : " + LocalDateTime.now());

        return apiErrorResponse;

    }


    // 2rd. Refresh토큰을 검사
    private ApiErrorResponse checkRefresh(String refresh){

        Predicate<String> checkingRefresh = stringParam -> StringUtils.isBlank(stringParam)&&
                jwtUtil.isExpired(stringParam)&&
                !jwtUtil.getCategory(stringParam).equals("refresh");

        Predicate<String> checkNull = StringParam
                -> StringUtils.isBlank(StringParam); // NULL 체크하기

        Predicate<String> checkExpiration = StringParam
                -> jwtUtil.isExpired(StringParam); // Expiration 여부 확인하기

        Predicate<String> checkCategory = StringParam
                -> !"refresh".equals(jwtUtil.getCategory(StringParam)); // 카테고리 일치여부 확인하기


        ApiErrorResponse apiErrorResponse =
                new ApiErrorResponse(false,
                        "Method ",
                        "Refresh NULL : " + checkNull.test(refresh) + "\n"
                                +"Expiration : " + checkExpiration.test(refresh) + "\n"
                                +"Category : " + checkCategory.test(refresh),
                        ""
                        );

        return apiErrorResponse;
    }





}
