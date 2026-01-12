package com.unithon.aeio.domain.classes.service;

import com.unithon.aeio.domain.classes.dto.ClassRequest;
import com.unithon.aeio.domain.classes.dto.ClassResponse;
import com.unithon.aeio.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClassService {
    ClassResponse.ClassId createClass(ClassRequest.ClassInfo request);
    ClassResponse.MemberClassId subsClass(Long classId, Member member);
    ClassResponse.LikeInfo likeClass(Long classId, Member member);
    ClassResponse.ClassId cancelLike(Long classId, Member member);
    ClassResponse.SubsList getMySubsList(Member member);
    Page<ClassResponse.ClassInfo> getMyLikedClasses(Member member, Pageable pageable);
    ClassResponse.MemberClassId unsubsClass(Long classId, Member member);
    ClassResponse.ClassId deleteClass(Long classId);
    ClassResponse.ClassInfo getClassInfo(Long classId);
}
