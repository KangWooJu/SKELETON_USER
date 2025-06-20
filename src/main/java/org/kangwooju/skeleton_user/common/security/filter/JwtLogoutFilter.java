package org.kangwooju.skeleton_user.common.security.filter;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kangwooju.skeleton_user.common.security.filter.response.FilterErrorResponse;
import org.kangwooju.skeleton_user.common.security.filter.response.FilterSuccessResponse;
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
@Slf4j
public class JwtLogoutFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ReissueService reissueService;


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)
            throws ServletException {
        return !("/logout".equals(request.getRequestURI())
                &&"POST".equalsIgnoreCase(request.getMethod()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("[ 로그아웃 필터 실행 ] URI : {} , TIME : {} "
                ,request.getRequestURI()
                ,LocalDateTime.now());

        String refresh = reissueService.findCookie(request);
        log.info("[ Refresh Token ] : " + refresh);

        // Refresh토큰에 오류가 있는지 검사
        if(checkRefresh(refresh).error()){
            log.info(" [ Refresh Token 오류 발생 ] TIME : {} ",LocalDateTime.now());
            filterChain.doFilter(request,response);
            return;
        }

        // Refresh 삭제하기
        reissueService.deleteRefresh(refresh);
        // Cookie를 빈 쿠키로 설정하기
        reissueService.zeroCookie(response);
        // 로그아웃 성공 Response반환
        JsonResponseUtils.writeJsonResponse(HttpStatus.OK,
                response,
                new FilterSuccessResponse(true,
                        "Method : /logout ",
                        "로그아웃에 성공하였습니다.",
                        LocalDateTime.now().toString())
                );
        log.info(" [ 로그아웃 성공 ] TIME : {}",LocalDateTime.now());
    }

    // 2rd. Refresh토큰을 검사
    private FilterErrorResponse checkRefresh(String refresh){

        Predicate<String> checkNull = StringParam
                -> StringUtils.isBlank(StringParam); // NULL 체크하기

        Predicate<String> checkExpiration = StringParam
                -> jwtUtil.isExpired(StringParam); // Expiration 여부 확인하기

        Predicate<String> checkCategory = StringParam
                -> !"refresh".equals(jwtUtil.getCategory(StringParam)); // 카테고리 일치여부 확인하기

        String strCheckNull = ""+checkNull.test(refresh);
        String strCheckExpiration = "" + checkExpiration.test(refresh);
        String strCheckCategory = "" + checkCategory.test(refresh);

        log.info("Method : POST , "+
                "Refresh NULL : " + strCheckNull + " ,"
                        +"Expiration : " + strCheckExpiration + " ,"
                        +"Category : " + strCheckCategory);

        boolean result = (checkNull.test(refresh)
                &&checkCategory.test(refresh)
                &&checkExpiration.test(refresh));


        FilterErrorResponse apiErrorResponse =
                new FilterErrorResponse(result,
                        "Method : POST",
                        "Refresh NULL : " + checkNull.test(refresh) + "\n"
                                +"Expiration : " + checkExpiration.test(refresh) + "\n"
                                +"Category : " + checkCategory.test(refresh),
                        ""
                        );

        return apiErrorResponse;
    }
}
