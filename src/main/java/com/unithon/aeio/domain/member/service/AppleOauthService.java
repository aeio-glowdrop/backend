package com.unithon.aeio.domain.member.service;

import com.unithon.aeio.domain.member.converter.MemberConverter;
import com.unithon.aeio.domain.member.dto.AppleInfoResponse;
import com.unithon.aeio.domain.member.dto.ApplePublicKeyDto;
import com.unithon.aeio.domain.member.dto.ApplePublicKeysResponse;
import com.unithon.aeio.domain.member.dto.AppleTokenResponseDto;
import com.unithon.aeio.domain.member.dto.OauthResponse;
import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.domain.member.repository.MemberRepository;
import com.unithon.aeio.global.error.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.List;

import static com.unithon.aeio.global.error.code.JwtErrorCode.APPLE_LOGIN_FAILED;
import static com.unithon.aeio.global.error.code.JwtErrorCode.INVALID_ACCESS_TOKEN;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AppleOauthService {

    private final MemberRepository memberRepository;
    private final JwtTokenService jwtTokenService;
    private final MemberConverter memberConverter;

    // WebClient 하나 안에서 재사용
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://appleid.apple.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=utf-8")
            .build();

    @Value("${oauth.apple.client-id}")
    private String clientId;

    @Value("${oauth.apple.team-id}")
    private String teamId;

    @Value("${oauth.apple.key-id}")
    private String keyId;

    @Value("${oauth.apple.private-key}")
    private String privateKey; // BASE64 본문

    private static final long THIRTY_DAYS_MS = 29L * 24 * 60 * 60 * 1000;

    public OauthResponse.ServerAccessTokenInfo loginApple(String code, HttpServletResponse response) {
        // 1. code -> 애플 토큰 교환
        AppleTokenResponseDto tokenResponse = getAppleToken(code);

        // 2. id_token 검증 + 유저정보 추출
        AppleInfoResponse appleInfo = parseAndVerifyIdToken(tokenResponse.getIdToken());

        // 3. authId(String) 기준으로 Member 조회/생성
        Member member = memberRepository.findByAuthId(appleInfo.getAuthId())
                .orElseGet(() -> createAppleMember(appleInfo));

        // 4. 우리 서버 access/refresh 발급 + 쿠키 세팅
        String accessToken = issueServerTokens(member, response);

        // 5. Kakao와 동일하게 ServerAccessTokenInfo로 래핑
        return memberConverter.toServerAccessTokenInfo(accessToken, member);
    }

    // ------------------ 내부 메서드들 ------------------

    private AppleTokenResponseDto getAppleToken(String code) {
        try {
            return webClient.post()
                    .uri("/auth/token")
                    .body(BodyInserters
                            .fromFormData("grant_type", "authorization_code")
                            .with("client_id", clientId)
                            .with("client_secret", makeClientSecretToken())
                            .with("code", code))
                    .retrieve()
                    .bodyToMono(AppleTokenResponseDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("[애플 로그인 실패] token 요청 실패: " + e.getResponseBodyAsString(), e);
            throw new BusinessException(APPLE_LOGIN_FAILED);
        }
    }

    private AppleInfoResponse parseAndVerifyIdToken(String idToken) {
        try {
            // 1. 애플 공개키 목록 가져오기
            List<ApplePublicKeyDto> publicKeys = getApplePublicKeys();

            // 2. kid -> 공개키 매칭하는 Locator
            MyKeyLocator keyLocator = new MyKeyLocator(publicKeys);

            // 3. JJWT 파서로 서명 검증 + payload 파싱
            Claims claims = Jwts.parser()
                    .keyLocator(keyLocator)
                    .requireIssuer("https://appleid.apple.com")
                    .requireAudience(clientId)
                    .build()
                    .parseSignedClaims(idToken)
                    .getPayload();

            Date exp = claims.getExpiration();
            if (exp != null && exp.before(new Date())) {
                throw new BusinessException(INVALID_ACCESS_TOKEN);
            }

            String sub = claims.getSubject();
            String email = claims.get("email", String.class);

            log.info("[애플 로그인] idToken 검증 완료: sub={}, email={}", sub, email);

            return AppleInfoResponse
                    .builder()
                    .authId(sub)
                    .email(email)
                    .build();

        } catch (Exception e) {
            log.error("[애플 로그인 실패] idToken 검증 실패", e);
            throw new BusinessException(APPLE_LOGIN_FAILED);
        }
    }

    private List<ApplePublicKeyDto> getApplePublicKeys() {
        try {
            ApplePublicKeysResponse response = webClient.get()
                    .uri("/auth/keys")
                    .retrieve()
                    .bodyToMono(ApplePublicKeysResponse.class)
                    .block();

            if (response == null || response.getKeys() == null || response.getKeys().isEmpty()) {
                throw new BusinessException(APPLE_LOGIN_FAILED);
            }
            return response.getKeys();
        } catch (WebClientResponseException e) {
            log.error("[애플 로그인 실패] public key 조회 실패: " + e.getResponseBodyAsString(), e);
            throw new BusinessException(APPLE_LOGIN_FAILED);
        }
    }

    private String makeClientSecretToken() {
        try {
            String token = Jwts.builder()
                    .subject(clientId) // sub
                    .issuer(teamId)    // iss
                    .issuedAt(new Date())  // iat
                    .expiration(new Date(System.currentTimeMillis() + THIRTY_DAYS_MS)) // exp
                    .audience().add("https://appleid.apple.com").and() // aud
                    .header().keyId(keyId).and() // kid
                    .signWith(getPrivateKey(), Jwts.SIG.ES256)         // alg=ES256
                    .compact();

            log.info("[애플 로그인] client_secret 생성 완료");
            return token;
        } catch (Exception e) {
            log.error("[애플 로그인 실패] client_secret 생성 실패", e);
            throw new BusinessException(APPLE_LOGIN_FAILED);
        }
    }

    private PrivateKey getPrivateKey() {
        try {
            byte[] privateKeyBytes = Decoders.BASE64.decode(privateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            log.error("[애플 로그인 실패] private key 생성 실패", e);
            throw new BusinessException(APPLE_LOGIN_FAILED);
        }
    }

    private Member createAppleMember(AppleInfoResponse appleInfo) {
        Member member = memberConverter.toAppleUserEntity(appleInfo);
        member.setAuthId(appleInfo.getAuthId());   // String
        member.setEmail(appleInfo.getEmail());
        return memberRepository.save(member);
    }

    private String issueServerTokens(Member member, HttpServletResponse response) {
        final String accessToken = jwtTokenService.createAccessToken(member.getAuthId());
        final String refreshToken = jwtTokenService.createRefreshToken();

        member.setAccessToken(accessToken);
        member.setRefreshToken(refreshToken);
        memberRepository.save(member);

        jwtTokenService.addRefreshTokenToCookie(refreshToken, response);

        return accessToken;
    }
}