package com.unithon.aeio.domain.member.service;

import com.unithon.aeio.domain.member.dto.MemberRequest;
import com.unithon.aeio.domain.member.dto.MemberResponse;
import com.unithon.aeio.domain.member.dto.OauthRequest;
import com.unithon.aeio.domain.member.dto.OauthResponse;
import com.unithon.aeio.domain.member.entity.Member;

public interface MemberService {
    MemberResponse.MemberId createMember(MemberRequest.MemberInfo request, Member member);
    MemberResponse.NickName getNickName(Member member);
    MemberResponse.Streak getStreak(Member member);
    MemberResponse.MemberId updateMember(MemberRequest.UpdateMemberInfo request, Member member);
    MemberResponse.MemberId deleteMember(Member member);
    OauthResponse.CheckMemberRegistration saveUserAgreements(Member member, OauthRequest.AgreementRequest request);
    MemberResponse.NickName updateNickName(Member member, String nickname);
    MemberResponse.MemberId updateProfile(Member member, String profileImageUrl);
}
