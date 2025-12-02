package com.unithon.aeio.domain.member.service;


import com.unithon.aeio.domain.member.dto.OauthRequest;
import com.unithon.aeio.domain.member.dto.OauthResponse;
import com.unithon.aeio.domain.member.entity.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface OauthService {
    OauthResponse.ServerAccessTokenInfo login(OauthRequest.FrontAccessTokenInfo oauthRequest, HttpServletResponse response);
    String refreshAccessToken(String refreshToken);
    String getTokens(String id, HttpServletResponse response);
    String loginWithKakao(String accessToken, HttpServletResponse response);
    OauthResponse.RefreshTokenResponse tokenRefresh(HttpServletRequest request);
    OauthResponse.CheckMemberRegistration checkRegistration(OauthRequest.LoginRequest request);
    OauthResponse.CheckMemberRegistration saveUserAgreements(Member member, OauthRequest.AgreementRequest request);
}