package com.unithon.aeio.domain.member.service;

import com.unithon.aeio.domain.member.dto.MemberRequest;
import com.unithon.aeio.domain.member.dto.MemberResponse;
import com.unithon.aeio.domain.member.entity.Member;

import java.time.LocalDate;

public interface MemberService {
    MemberResponse.MemberId createMember(MemberRequest.MemberInfo request, Member member);
    MemberResponse.NickName getNickName(Member member);
    MemberResponse.Streak getStreak(Member member);
    MemberResponse.MemberId updateMember(MemberRequest.UpdateMemberInfo request, Member member);
    MemberResponse.MemberId deleteMember(Member member);
}
