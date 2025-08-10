package com.unithon.aeio.domain.classes.service;

import com.unithon.aeio.domain.classes.dto.ClassRequest;
import com.unithon.aeio.domain.classes.dto.ClassResponse;
import com.unithon.aeio.domain.member.entity.Member;

public interface ClassService {
    ClassResponse.ClassId createClass(ClassRequest.ClassInfo request);
    ClassResponse.MemberClassId subsClass(Long classId, Member member);
}
