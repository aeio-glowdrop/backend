package com.unithon.aeio.domain.member.service;

import com.unithon.aeio.domain.member.dto.MemberRequest;
import com.unithon.aeio.domain.member.dto.MemberResponse;
import com.unithon.aeio.domain.member.entity.Member;

public interface MemberService {
    MemberResponse.MemberId createMember(MemberRequest.MemberInfo request, Member member);
}
