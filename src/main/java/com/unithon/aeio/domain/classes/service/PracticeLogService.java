package com.unithon.aeio.domain.classes.service;

import com.unithon.aeio.domain.classes.entity.Classes;
import com.unithon.aeio.domain.classes.entity.MemberClass;
import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.domain.classes.dto.PracticeLogRequest;
import com.unithon.aeio.domain.classes.dto.PracticeLogResponse;
import com.unithon.aeio.global.error.BusinessException;

import java.util.List;

import static com.unithon.aeio.global.error.code.JwtErrorCode.CLASS_NOT_FOUND;

public interface PracticeLogService {
    List<PracticeLogResponse.PreSignedUrl> getPreSignedUrlList(PracticeLogRequest.PreSignedUrlRequest request);
    PracticeLogResponse.PracticeLogId createBasicLog(Long classId, Member member, PracticeLogRequest.BasicLog request);
    Classes findClass(long classId);
    MemberClass findMemberClass(long memberId, long classId);
}
