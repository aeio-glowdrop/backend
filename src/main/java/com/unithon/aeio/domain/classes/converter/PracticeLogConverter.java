package com.unithon.aeio.domain.classes.converter;

import com.unithon.aeio.domain.classes.dto.PracticeLogResponse;
import com.unithon.aeio.domain.classes.entity.PracticeLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PracticeLogConverter {

    //presigned url 리스트
    public PracticeLogResponse.PreSignedUrlList toPreSignedUrlList(List<PracticeLogResponse.PreSignedUrl> preSignedUrlList) {
        List<PracticeLogResponse.PreSignedUrl> preSignedUrlInfoList = preSignedUrlList
                .stream()
                .map(preSignedUrlInfo -> toPreSignedUrl(
                        preSignedUrlInfo.getPreSignedUrl(),
                        preSignedUrlInfo.getPhotoUrl(),
                        preSignedUrlInfo.getPhotoName()
                ))
                .collect(Collectors.toList());

        return PracticeLogResponse.PreSignedUrlList
                .builder()
                .preSignedUrlInfoList(preSignedUrlInfoList)
                .build();
    }

    public PracticeLogResponse.PreSignedUrl toPreSignedUrl(String preSignedUrl, String photoUrl, String photoName) {
        return PracticeLogResponse.PreSignedUrl
                .builder()
                .preSignedUrl(preSignedUrl)
                .photoUrl(photoUrl)
                .photoName(photoName)
                .build();
    }

    // id만 반환
    public PracticeLogResponse.PracticeLogId toPracticeLogId(PracticeLog practiceLog) {
        return PracticeLogResponse.PracticeLogId
                .builder()
                .practiceLogId(practiceLog.getId())
                .build();
    }
}
