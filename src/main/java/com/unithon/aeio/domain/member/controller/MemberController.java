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
import static com.unithon.aeio.global.result.code.MemberResultCode.UPDATE_PROFILE;

@RestController
@RequestMapping("/members")
@Tag(name = "01. 회원 API", description = "회원 도메인의 API입니다.")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @Operation(summary = "회원정보 저장 API", description = "새로운 회원정보를 저장하는 API입니다.")
    public ResultResponse<MemberResponse.MemberId> createMember(@RequestBody @Valid MemberRequest.MemberInfo request,
                                                                @LoginMember Member member) {
        return ResultResponse.of(MemberResultCode.CREATE_MEMBER,
                memberService.createMember(request, member));
    }

    @PatchMapping
    @Operation(summary = "회원정보 수정 API", description = "회원 닉네임/성별/고민부위를 부분 수정합니다.")
    public ResultResponse<MemberResponse.MemberId> updateMember(
            @RequestBody @Valid MemberRequest.UpdateMemberInfo request,
            @LoginMember Member member
    ) {
        return ResultResponse.of(MemberResultCode.UPDATE_MEMBER,
                memberService.updateMember(request, member));
    }

    @GetMapping("/information")
    @Operation(summary = "사용자 정보 조회 API", description = "로그인한 사용자의 닉네임을 반환하는 API입니다.")
    public ResultResponse<MemberResponse.MemberInfo> getMemberInfo(@LoginMember Member member) {
        return ResultResponse.of(MemberResultCode.GET_USER_INFO,
                memberService.getMemberInfo(member));
    }

    @GetMapping("/streak")
    @Operation(summary = "현재 스트릭 조회", description = "오늘을 기준으로 연속 운동 일수를 반환합니다.")
    public ResultResponse<MemberResponse.Streak> getStreak(@LoginMember Member member) {
        return ResultResponse.of(GET_CURRENT_STREAK,
                memberService.getStreak(member));
    }

    // 멤버 삭제
    @DeleteMapping("")
    @Operation(summary = "멤버 삭제 API", description = "멤버를 delete 처리합니다.")
    public ResultResponse<MemberResponse.MemberId> deleteMember(@LoginMember Member member) {
        return ResultResponse.of(DELETE_MEMBER,
                memberService.deleteMember(member));
    }

    @PostMapping("/agreements")
    @Operation(summary = "이용정보 약관 동의 API", description = "이용정보 약관에 동의하는 API입니다.")
    public ResultResponse<OauthResponse.CheckMemberRegistration> saveAgreements(@LoginMember Member member,
                                                                                @Valid @RequestBody OauthRequest.AgreementRequest request
    ) throws BusinessException {

        if (member == null) {
            throw new BusinessException(UNAUTHORIZED);
        }

        return ResultResponse.of(AGREE, memberService.saveUserAgreements(member, request));
    }

    @PatchMapping("/nickname")
    @Operation(summary = "사용자 닉네임 수정 API", description = "로그인한 사용자의 닉네임을 수정하는 API입니다.")
    public ResultResponse<MemberResponse.MemberInfo> getNickName(@LoginMember Member member, @RequestParam String nickname) {
        return ResultResponse.of(MemberResultCode.UPDATE_NICKNAME,
                memberService.updateNickName(member, nickname));
    }

    @PostMapping("/profileImage")
    @Operation(summary = "프로필 사진 업로드/수정 API", description = "로그인한 사용자의 프로필 사진을 업로드/수정하는 API입니다.")
    public ResultResponse<MemberResponse.MemberId> updateProfileImage(@LoginMember Member member, @RequestBody MemberRequest.UpdateProfile request) {
        return ResultResponse.of(UPDATE_PROFILE,
                memberService.updateProfile(member, request.getProfileImageUrl()));
    }
}
