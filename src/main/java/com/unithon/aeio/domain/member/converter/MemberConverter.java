package com.unithon.aeio.domain.member.converter;


import com.unithon.aeio.domain.member.dto.AppleInfoResponse;
import com.unithon.aeio.domain.member.dto.GoogleInfoResponse;
import com.unithon.aeio.domain.member.dto.MemberResponse;
import com.unithon.aeio.domain.member.dto.OauthResponse;
import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.domain.member.entity.Provider;
import com.unithon.aeio.domain.member.entity.Worry;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class MemberConverter {

    // 인가 관련, DTO를 엔티티로 변환하는 메소드
    public Member toKakaoUserEntity(OauthResponse.KakaoInfo kakaoInfo) {
        return Member.builder()
                .authId(kakaoInfo.getAuthId())
                .name(kakaoInfo.getName())
                .email(kakaoInfo.getEmail())
                .refreshToken(kakaoInfo.getRefreshToken())
                .provider(Provider.KAKAO)
                .build();
    }

    public Member toAppleUserEntity(AppleInfoResponse appleInfo) {
        return Member.builder()
                .authId(appleInfo.getAuthId())
                .email(appleInfo.getEmail())
                .provider(Provider.APPLE)
                .build();
    }

    public Member toGoogleUserEntity(GoogleInfoResponse googleInfo) {
        return Member.builder()
                .authId(googleInfo.getAuthId())
                .name(googleInfo.getName())
                .email(googleInfo.getEmail())
                .provider(Provider.GOOGLE)
                .build();
    }

    // 로그인 시, 정보를 응답으로 변환하는 메소드
    public OauthResponse.ServerAccessTokenInfo toServerAccessTokenInfo(String accessToken, Member member) {
        // 응답객체 생성
        OauthResponse.ServerAccessTokenInfo response = new OauthResponse.ServerAccessTokenInfo();
        response.setAccessToken(accessToken);
        response.setMemberId(member.getId());
        return response;
    }

    // 인가 관련 응답을 DTO로 반환
    public OauthResponse.KakaoInfo toLoginUserInfo(Member member) {
        return OauthResponse.KakaoInfo.builder()
                .authId(member.getAuthId())
                .name(member.getName())
                .refreshToken(member.getRefreshToken())
                .build();
    }

    // 회원가입 여부 체크
    public OauthResponse.CheckMemberRegistration toCheckMemberRegistration(boolean isRegistered) {
        return new OauthResponse.CheckMemberRegistration(isRegistered);
    }

    // member Id만 반환
    public MemberResponse.MemberId toMemberId(Member member) {
        return MemberResponse.MemberId
                .builder()
                .memberId(member.getId())
                .build();
    }

    public MemberResponse.MemberInfo toMemberInfo(Member member, List<String> worryList, String profileURL) {
        return MemberResponse.MemberInfo
                .builder()
                .nickName(member.getNickname())
                .profileURL(profileURL)
                .email(member.getEmail())
                .memberId(member.getId())
                .worryList(worryList)
                .build();
    }


    public MemberResponse.NickName toNickName(Member member) {
        return MemberResponse.NickName
                .builder()
                .nickName(member.getNickname())
                .memberId(member.getId())
                .build();
    }

    public MemberResponse.Streak toStreak(LocalDate today, int streak) {
        return MemberResponse.Streak
                .builder()
                .today(today)
                .streakDays(streak)
                .build();
    }
}

