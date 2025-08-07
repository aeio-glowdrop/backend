package com.unithon.aeio.global.security;

import com.unithon.aeio.domain.member.entity.UserRole;
import com.unithon.aeio.domain.member.repository.MemberRepository;
import com.unithon.aeio.domain.member.service.JwtTokenService;
import com.unithon.aeio.global.security.filter.JwtFilter;
import com.unithon.aeio.global.security.handler.ExceptionHandlerFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@RequiredArgsConstructor
@Configuration
// SecurityConfig 클래스는 Spring Security의 설정을 정의하는 설정 클래스
// 애플리케이션의 보안 정책을 설정하고, 인증 및 권한 부여와 관련된 설정을 관리
// JWT 기반의 인증을 적용하고, 특정 경로에 대한 접근 권한을 설정하며, Swagger 경로와 같은 특정 경로를 필터링에서 제외하는 역할
public class SecurityConfig {
    private final JwtTokenService jwtTokenService;
    private final MemberRepository memberRepository;

    // AuthenticationManager를 빈으로 등록. 이 빈은 인증을 처리하는 데 사용됨
    @Bean
    public AuthenticationManager authenticationManager(
            final AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // authenticationConfiguration을 통해 AuthenticationManager를 반환
        return authenticationConfiguration.getAuthenticationManager();
    }

    // SecurityFilterChain을 빈으로 등록하여 보안 설정을 정의
    @Bean
    public SecurityFilterChain configure(final HttpSecurity http) throws Exception {
        return http.cors(withDefaults()) // CORS 설정을 기본값으로 설정
                .csrf((csrf) -> csrf.disable()) // CSRF 보호를 비활성화
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/error/**").permitAll()
                        .requestMatchers("/login/**").permitAll() // 앱 링크 검증 요청은 안드로이드 시스템(Google 서버)에서 비로그인 상태로 HTTP GET 요청
                        .requestMatchers("/.well-known/assetlinks.json").permitAll() // 앱링크 경로도 인증 없이 접근 가능 (
                        .requestMatchers("/user/**").hasAuthority(UserRole.USER.getRole()) // /user/** 경로는 USER 권한이 있어야 접근 가능
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/v3/api-docs/**").permitAll() // 스웨거 경로는 인증 없이 접근 가능
                        .anyRequest().authenticated()) // 그 외 모든 요청은 인증이 필요
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 사용하지 않고, JWT로 인증을 관리
                .formLogin(AbstractHttpConfigurer::disable) // 로그인 폼을 사용하지 않도록 설정
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP 기본 인증을 비활성화
                .addFilterBefore(new JwtFilter(jwtTokenService, memberRepository), UsernamePasswordAuthenticationFilter.class) // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
                .addFilterBefore(new ExceptionHandlerFilter(), JwtFilter.class) // JWT 필터 앞에 예외 처리 필터를 추가
                .build(); // 설정을 기반으로 SecurityFilterChain 객체를 생성
    }

    // WebSecurityCustomizer를 빈으로 등록하여 특정 요청 경로를 보안 필터에서 제외
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        // 아래 url은 filter 에서 제외
        return web ->
                web.ignoring()
                        .requestMatchers("/login/**") // 로그인 및 토큰 갱신 경로는 보안 필터에서 제외
                        .requestMatchers("/auth/**") //auth관련 경로도 제외
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/.well-known/assetlinks.json", // 앱링크 경로 제외
                                "/v3/api-docs/**" // 스웨거 경로도 보안 필터에서 제외
                        );
    }
}

