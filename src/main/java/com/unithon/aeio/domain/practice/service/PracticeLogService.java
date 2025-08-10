package com.unithon.aeio.domain.practice.service;

import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.domain.practice.dto.PracticeLogRequest;
import com.unithon.aeio.domain.practice.dto.PracticeLogResponse;

import java.util.List;

public interface PracticeLogService {
    List<PracticeLogResponse.PreSignedUrl> getPreSignedUrlList(PracticeLogRequest.PreSignedUrlRequest request);
}
