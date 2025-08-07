package com.unithon.aeio.domain.member.controller;

import com.unithon.aeio.domain.member.converter.MemberConverter;
import com.unithon.aeio.domain.member.dto.OauthRequest;
import com.unithon.aeio.domain.member.dto.OauthResponse;
import com.unithon.aeio.domain.member.service.OauthService;
import com.unithon.aeio.global.result.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.unithon.aeio.global.result.code.MemberResultCode.CHECK_MEMBER_REGISTRATION;
import static com.unithon.aeio.global.result.code.MemberResultCode.LOGIN;
import static com.unithon.aeio.global.result.code.MemberResultCode.REFRESH_TOKEN;

@RestController
@RequestMapping
@Tag(name = "00. 인증,인가 관련 API", description = "회원의 회원가입 및 로그인 등을 처리하는 API입니다.")
@RequiredArgsConstructor
public class OauthController {

    private final OauthService oauthService;

    @PostMapping("/login/oauth")
    @Operation(summary = "로그인 API", description = "카카오톡을 통해 서비스에 로그인하는 API입니다.")
    public ResultResponse<OauthResponse.ServerAccessTokenInfo> login(@RequestBody OauthRequest.FrontAccessTokenInfo oauthRequest,
                                                                     HttpServletResponse response) { //HTTP 응답 조작: Refresh Token을 클라이언트의 브라우저 쿠키에 저장할 때 사용
        return ResultResponse.of(LOGIN, oauthService.login(oauthRequest, response));
    }

    // 리프레시 토큰으로 액세스토큰 재발급 받는 로직
    @PostMapping("/login/token/refresh")
    @Operation(summary = "액세스토큰 재발급 API", description = "리프레시 토큰으로 액세스토큰을 재발급 받는 API입니다.")
    public ResultResponse<OauthResponse.RefreshTokenResponse> tokenRefresh(HttpServletRequest request) { //request: 클라이언트가 서버에 보낸 HTTP 요청에 포함된 쿠키를 가져오기 위함
        return ResultResponse.of(REFRESH_TOKEN, oauthService.tokenRefresh(request));
    }

    // 특정 authId를 가진 회원의 회원가입 여부 조회
    @PostMapping("/auth/check-registration")
    @Operation(summary = "회원가입 여부 조회 API", description = "authId를 통해, 해당 정보와 일치하는 회원의 가입 여부를 조회하는 API입니다.")
    public ResultResponse<OauthResponse.CheckMemberRegistration> checkSignup(@Valid @RequestBody OauthRequest.LoginRequest request) {
        return ResultResponse.of(CHECK_MEMBER_REGISTRATION, oauthService.checkRegistration(request));
    }

}
