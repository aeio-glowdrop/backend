package com.unithon.aeio.domain.member.controller;

import com.unithon.aeio.domain.member.dto.MemberRequest;
import com.unithon.aeio.domain.member.dto.MemberResponse;
import com.unithon.aeio.domain.member.dto.OauthRequest;
import com.unithon.aeio.domain.member.dto.OauthResponse;
import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.domain.member.service.MemberService;
import com.unithon.aeio.global.error.BusinessException;
import com.unithon.aeio.global.result.ResultResponse;
import com.unithon.aeio.global.result.code.MemberResultCode;
import com.unithon.aeio.global.security.annotation.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.unithon.aeio.global.error.code.GlobalErrorCode.UNAUTHORIZED;
import static com.unithon.aeio.global.result.code.MemberResultCode.AGREE;
import static com.unithon.aeio.global.result.code.MemberResultCode.DELETE_MEMBER;
import static com.unithon.aeio.global.result.code.MemberResultCode.GET_CURRENT_STREAK;
import static com.unithon.aeio.global.result.code.MemberResultCode.GET_MYPAGE;
import static com.unithon.aeio.global.result.code.MemberResultCode.GET_WORRY_LIST;
import static com.unithon.aeio.global.result.code.MemberResultCode.UPDATE_PROFILE;
import static com.unithon.aeio.global.result.code.MemberResultCode.UPDATE_WORRY_LIST;

@RestController
@RequestMapping("/members")
@Tag(name = "01. 회원 API", description = "회원 도메인의 API입니다.")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @Operation(summary = "회원정보 저장 API (최초 온보딩)", description = "최초 가입 시 닉네임/성별/연령대/고민부위 등 프로필을 저장합니다.")
    public ResultResponse<MemberResponse.MemberId> createMember(@RequestBody @Valid MemberRequest.MemberInfo request,
                                                                @LoginMember Member member) {
        return ResultResponse.of(MemberResultCode.CREATE_MEMBER,
                memberService.createMember(request, member));
    }

    @PatchMapping
    @Operation(summary = "회원정보 부분 수정 API", description = "닉네임/성별/고민부위를 부분 수정합니다.")
    public ResultResponse<MemberResponse.MemberId> updateMember(
            @RequestBody @Valid MemberRequest.UpdateMemberInfo request,
            @LoginMember Member member
    ) {
        return ResultResponse.of(MemberResultCode.UPDATE_MEMBER,
                memberService.updateMember(request, member));
    }

    @GetMapping("/information")
    @Operation(summary = "회원정보 조회 API", description = "로그인 사용자의 상세 프로필(닉네임/프로필사진/이메일/고민부위)을 조회합니다.")
    public ResultResponse<MemberResponse.MemberInfo> getMemberInfo(@LoginMember Member member) {
        return ResultResponse.of(MemberResultCode.GET_USER_INFO,
                memberService.getMemberInfo(member));
    }

    @GetMapping("/nickName")
    @Operation(summary = "회원 닉네임 조회 API", description = "닉네임만 간단 조회합니다.")
    public ResultResponse<MemberResponse.NickName> getNickName(@LoginMember Member member) {
        return ResultResponse.of(MemberResultCode.GET_NICKNAME,
                memberService.getNickName(member));
    }

    @GetMapping("/streak")
    @Operation(summary = "연속 운동일수(스트릭) 조회 API", description = "오늘 기준 연속 운동일수를 계산합니다(전체 클래스 통합 기준)")
    public ResultResponse<MemberResponse.Streak> getStreak(@LoginMember Member member) {
        return ResultResponse.of(GET_CURRENT_STREAK,
                memberService.getStreak(member));
    }

    // 멤버 삭제
    @DeleteMapping
    @Operation(summary = "회원 삭제 API", description = "회원 탈퇴 처리를 합니다.")
    public ResultResponse<MemberResponse.MemberId> deleteMember(@LoginMember Member member) {
        return ResultResponse.of(DELETE_MEMBER,
                memberService.deleteMember(member));
    }

    @PostMapping("/agreements")
    @Operation(summary = "이용약관 동의 API", description = "이용약관/개인정보처리방침/마케팅 수신 등 동의를 저장합니다.")
    public ResultResponse<OauthResponse.CheckMemberRegistration> saveAgreements(@LoginMember Member member,
                                                                                @Valid @RequestBody OauthRequest.AgreementRequest request
    ) throws BusinessException {

        if (member == null) {
            throw new BusinessException(UNAUTHORIZED);
        }

        return ResultResponse.of(AGREE, memberService.saveUserAgreements(member, request));
    }

    @PatchMapping("/nickname")
    @Operation(summary = "사용자 닉네임 수정 API", description = "닉네임만 단독 수정합니다.")
    public ResultResponse<MemberResponse.NickName> getNickName(@LoginMember Member member, @RequestParam String nickname) {
        return ResultResponse.of(MemberResultCode.UPDATE_NICKNAME,
                memberService.updateNickName(member, nickname));
    }

    @PostMapping("/profileImage")
    @Operation(summary = "프로필 사진 업로드/수정 API", description = "S3에 미리 업로드된 사진 URL을 회원 프로필에 반영합니다.")
    public ResultResponse<MemberResponse.MemberId> updateProfileImage(@LoginMember Member member, @RequestBody MemberRequest.UpdateProfile request) {
        return ResultResponse.of(UPDATE_PROFILE,
                memberService.updateProfile(member, request.getProfileImageUrl()));
    }

    @GetMapping("/mypage")
    @Operation(summary = "마이페이지 조회 API", description = "마이페이지에 필요한 통계(운동횟수/시간/리뷰수/구독수/좋아요수)를 종합 조회합니다.")
    public ResultResponse<MemberResponse.MyPage> getMyPage(@LoginMember Member member) {
        return ResultResponse.of(GET_MYPAGE, memberService.getMyPage(member));
    }

    @GetMapping("/worryList")
    @Operation(summary = "고민 부위 조회 API", description = "사용자가 선택한 고민 부위를 조회합니다.")
    public ResultResponse<MemberResponse.WorryList> getWorryList(@LoginMember Member member) {
        return ResultResponse.of(GET_WORRY_LIST, memberService.getWorryList(member));
    }

    @PatchMapping("/worryList")
    @Operation(summary = "고민 부위 수정 API", description = "고민 부위 목록을 전체 교체합니다.")
    public ResultResponse<MemberResponse.WorryList> updateWorryList(@LoginMember Member member,
                                                                     @RequestBody @Valid MemberRequest.UpdateWorryList request) {
        return ResultResponse.of(UPDATE_WORRY_LIST, memberService.updateWorryList(member, request));
    }
}
