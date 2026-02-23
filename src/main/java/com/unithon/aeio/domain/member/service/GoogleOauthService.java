package com.unithon.aeio.domain.member.service;

import com.unithon.aeio.domain.member.converter.MemberConverter;
import com.unithon.aeio.domain.member.dto.GoogleInfoResponse;
import com.unithon.aeio.domain.member.dto.OauthResponse;
import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.domain.member.entity.Provider;
import com.unithon.aeio.domain.member.repository.MemberRepository;
import com.unithon.aeio.global.error.BusinessException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static com.unithon.aeio.global.error.code.JwtErrorCode.GOOGLE_LOGIN_FAILED;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GoogleOauthService {

    private static final String GOOGLE_USERINFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";

    private final MemberRepository memberRepository;
    private final JwtTokenService jwtTokenService;
    private final MemberConverter memberConverter;

    public OauthResponse.ServerAccessTokenInfo loginGoogle(String accessToken, HttpServletResponse response) {
        GoogleInfoResponse googleInfo = getGoogleUserInfo(accessToken);

        Member member = memberRepository.findByAuthId(googleInfo.getAuthId())
                .orElseGet(() -> memberRepository.save(memberConverter.toGoogleUserEntity(googleInfo)));

        member.setName(googleInfo.getName());
        member.setEmail(googleInfo.getEmail());
        member.setProvider(Provider.GOOGLE);
        memberRepository.save(member);

        String serverAccessToken = issueServerTokens(member, response);

        return memberConverter.toServerAccessTokenInfo(serverAccessToken, member);
    }

    private GoogleInfoResponse getGoogleUserInfo(String accessToken) {
        try {
            return WebClient.create()
                    .get()
                    .uri(GOOGLE_USERINFO_URL)
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .bodyToMono(GoogleInfoResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("[Google 로그인 실패] userinfo 요청 실패: {}", e.getResponseBodyAsString(), e);
            throw new BusinessException(GOOGLE_LOGIN_FAILED);
        }
    }

    private String issueServerTokens(Member member, HttpServletResponse response) {
        final String serverAccessToken = jwtTokenService.createAccessToken(member.getAuthId());
        final String refreshToken = jwtTokenService.createRefreshToken();

        member.setAccessToken(serverAccessToken);
        member.setRefreshToken(refreshToken);
        memberRepository.save(member);

        jwtTokenService.addRefreshTokenToCookie(refreshToken, response);

        return serverAccessToken;
    }
}
