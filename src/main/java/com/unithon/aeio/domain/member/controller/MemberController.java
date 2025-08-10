package com.unithon.aeio.domain.member.controller;

import com.unithon.aeio.domain.member.dto.MemberRequest;
import com.unithon.aeio.domain.member.dto.MemberResponse;
import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.domain.member.service.MemberService;
import com.unithon.aeio.global.result.ResultResponse;
import com.unithon.aeio.global.result.code.MemberResultCode;
import com.unithon.aeio.global.security.annotation.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
