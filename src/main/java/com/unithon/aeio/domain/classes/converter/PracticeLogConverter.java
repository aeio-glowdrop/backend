package com.unithon.aeio.domain.classes.converter;

import com.unithon.aeio.domain.classes.dto.PracticeLogResponse;
import com.unithon.aeio.domain.classes.entity.Classes;
import com.unithon.aeio.domain.classes.entity.MemberClass;
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

    //일일 운동 정보 반환
    public PracticeLogResponse.PracticeItem toItem(PracticeLog log) {
        MemberClass memberClass = log.getMemberClass();
        Classes classes = memberClass.getClasses();

        return PracticeLogResponse.PracticeItem
                .builder()
                .practiceLogId(log.getId())
                .memberClassId(memberClass.getId())
                .classId(classes.getId())
                .className(classes.getClassName())
                .classType(classes.getClassType() != null ? classes.getClassType().name() : null)
                .count(log.getCount())
                .feedback(log.getFeedBack())
                .expressionlessPhoto(log.getExpressionlessPhoto())
                .practicePhoto(log.getPracticePhoto())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
