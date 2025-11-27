package com.unithon.aeio.domain.member.service;

import com.unithon.aeio.global.error.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Random;

import static com.unithon.aeio.global.error.code.GlobalErrorCode.UNAUTHORIZED;

@Service
public class JwtTokenService implements InitializingBean {
    // JWT(JSON Web Token)의 생성, 검증 및 관리와 관련된 기능을 제공하는 서비스 클래스
    // JWT 토큰을 생성하여 사용자를 인증하고, 발급된 토큰이 유효한지 검증하며, 토큰의 만료 여부를 확인
    // Access Token과 Refresh Token을 생성하고, 클라이언트에게 Refresh Token을 쿠키로 제공하는 등의 기능도 수행

    private final long accessTokenExpirationInSeconds; //액세스 토큰의 만료 시간(초단위)
    private final long refreshTokenExpirationInSeconds;
    private final String secretKey; //비밀 키
    private static SecretKey key; //암호화 키 객체

    // 생성자: JWT 토큰의 만료 시간 및 비밀 키를 설정
    public JwtTokenService(
            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenExpirationInSeconds,
            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenExpirationInSeconds,
            @Value("${jwt.secret}") String secretKey
    ) {
        // 설정된 초 단위를 밀리초로 변환하여 저장
        this.accessTokenExpirationInSeconds = accessTokenExpirationInSeconds * 1000;
        this.refreshTokenExpirationInSeconds = refreshTokenExpirationInSeconds * 1000;
        this.secretKey = secretKey;
    }

    // InitializingBean 인터페이스의 메소드: 모든 빈의 프로퍼티(필드)가 설정된 후 호출됨
    // 여기서 빈은 JwtTokenService 클래스의 인스턴스
    // 빈이 초기화된 후 secretKey를 기반으로 HMAC-SHA 키 객체를 생성
    @Override
    public void afterPropertiesSet() {
        key = getKeyFromBase64EncodedKey(encodeBase64SecretKey(secretKey));
    }

    // 액세스 토큰을 생성하는 메소드
    public String createAccessToken(String payload){
        // 주어진 payload와 설정된 만료 시간을 사용하여 JWT 토큰을 생성
        return createToken(payload, accessTokenExpirationInSeconds);
    }

    // 리프레시 토큰을 생성하는 메소드
    public String createRefreshToken(){
        // 랜덤하게 생성된 문자열을 사용하여 리프레시 토큰을 생성
        byte[] array = new byte[7];
        new Random().nextBytes(array);
        String generatedString = new String(array, StandardCharsets.UTF_8);
        // 생성된 문자열과 설정된 만료 시간을 사용하여 JWT 토큰을 생성
        return createToken(generatedString, refreshTokenExpirationInSeconds);
    }

    // JWT 토큰을 생성하는 메소드
    public String createToken(String payload, long expireLength){

        // JWT 클레임을 설정. payload는 subject로 설정됨 (토큰이 발급된 대상인 'sub'클레임을 뜻함)
        Date now = new Date();
        Date validity = new Date(now.getTime() + expireLength);

        // JWT 토큰을 생성하고 서명한 후, 문자열로 반환
        return Jwts.builder()
                .subject(payload)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key, Jwts.SIG.HS256)  // 🔥 추천 방식
                .compact();
    }

    // // JWT 토큰에서 payload를 추출하는 메소드
    public String getPayload(String token){
        try{
            // 주어진 토큰을 파싱하고, 그 토큰의 subject (payload)를 반환
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims((token))
                    .getPayload()
                    .getSubject();
        }catch (ExpiredJwtException e){
            // 토큰이 만료되었을 때, 만료된 토큰의 subject를 반환
            // 만료된 토큰이더라도 그 토큰에 담긴 주체 정보가 여전히 유효하고,
            // 이를 기반으로 추가적인 처리(로그아웃 처리, 만료된 토큰으로 새 토큰 발급 등)를 할 수 있도록 하기 위함
            return e.getClaims().getSubject();
        }catch (JwtException e){
            // JWT 관련 예외가 발생하면 인증되지 않은 상태로 처리
            throw new BusinessException(UNAUTHORIZED);
        }
    }

    // 주어진 토큰이 유효한지 검증하는 메소드
    public boolean validateToken(String token){
        try{
            // 토큰을 파싱하고, 만료되지 않았는지 확인
            Jws<Claims> jws = Jwts
                    .parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            // 만료 시간이 현재시간 이전이면 true -> 만료되었단 뜻이므로 반대로 false를 반환. 만료되지 않았으면 ture
            return !jws.getPayload().getExpiration().before(new Date());
        }catch (JwtException | IllegalArgumentException exception){
            // 토큰이 유효하지 않으면 false를 반환
            return false;
        }
    }


    // 비밀 키를 Base64로 인코딩하는 메소드
    private String encodeBase64SecretKey(String secretKey) {
        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // Base64로 인코딩된 비밀 키에서 Key 객체를 생성하는 메소드
    private SecretKey getKeyFromBase64EncodedKey(String base64EncodedSecretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 클라이언트의 쿠키에 리프레시 토큰을 저장하는 메소드
    public void addRefreshTokenToCookie(String refreshToken, HttpServletResponse response) {
        int maxAge = (int) (refreshTokenExpirationInSeconds / 1000);

        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}

