package com.unithon.aeio.domain.classes.service;

import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.domain.classes.dto.PracticeLogRequest;
import com.unithon.aeio.domain.classes.dto.PracticeLogResponse;

import java.util.List;

public interface PracticeLogService {
    List<PracticeLogResponse.PreSignedUrl> getPreSignedUrlList(PracticeLogRequest.PreSignedUrlRequest request);
    PracticeLogResponse.PracticeLogId createBasicLog(Long classId, Member member, PracticeLogRequest.BasicLog request);
}
