package com.unithon.aeio.domain.practice.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.unithon.aeio.domain.classes.entity.Classes;
import com.unithon.aeio.domain.classes.repository.ClassRepository;
import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.domain.practice.converter.PracticeLogConverter;
import com.unithon.aeio.domain.practice.dto.PracticeLogRequest;
import com.unithon.aeio.domain.practice.dto.PracticeLogResponse;
import com.unithon.aeio.domain.practice.entity.PracticeLog;
import com.unithon.aeio.domain.practice.repository.PracticeLogRepository;
import com.unithon.aeio.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.unithon.aeio.global.error.code.JwtErrorCode.CLASS_NOT_FOUND;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PracticeLogServiceImpl implements PracticeLogService {

    private final AmazonS3 amazonS3;
    private final PracticeLogConverter practiceLogConverter;
    private final ClassRepository classRepository;
    private final PracticeLogRepository practiceLogRepository;

    @Value("${spring.cloud.aws.s3.photo-bucket}")
    private String BUCKET_NAME;
    @Value("${spring.cloud.aws.region.static}")
    private String REGION;

    public static final String RAW_PATH_PREFIX = "photo";

    // presigned url 리스트
    @Override
    public List<PracticeLogResponse.PreSignedUrl> getPreSignedUrlList(PracticeLogRequest.PreSignedUrlRequest request) {
        return request.getPhotoNameList()
                .stream()
                .map(this::getPreSignedUrl)
                .collect(Collectors.toList());
    }

    // presigned url
    private PracticeLogResponse.PreSignedUrl getPreSignedUrl(String originalFilename) {
        String fileName = createPath(originalFilename);
        String photoName = fileName.split("/")[1];
        String photoUrl = generateFileAccessUrl(fileName);

        URL preSignedUrl = amazonS3.generatePresignedUrl(getGeneratePreSignedUrlRequest(BUCKET_NAME, fileName));
        return practiceLogConverter.toPreSignedUrl(preSignedUrl.toString(), photoUrl, photoName);
    }

    // 원본 사진 전체 경로 생성
    private String createPath(String fileName) {
        String fileId = createFileId();
        return String.format("%s/%s", RAW_PATH_PREFIX, fileId + fileName);
    }

    // 사진 고유 ID 생성
    private String createFileId() {
        return UUID.randomUUID().toString();
    }

    // 원본 사진의 접근 URL 생성
    private String generateFileAccessUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", BUCKET_NAME, REGION, fileName);
    }

    // 사진 업로드용(PUT) PreSigned URL 생성
    private GeneratePresignedUrlRequest getGeneratePreSignedUrlRequest(String bucket, String fileName) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(getPreSignedUrlExpiration());
        generatePresignedUrlRequest.addRequestParameter(Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead.toString());

        return generatePresignedUrlRequest;
    }

    // PreSigned URL 유효 기간 설정
    private Date getPreSignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 3;
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    // ---------- 여기까지 presigned

    @Override
    public PracticeLogResponse.PracticeLogId createBasicLog(Long classId, Member member, PracticeLogRequest.BasicLog request) {
        // 클래스 존재 확인
        Classes classes = findClass(classId);

        // practiceLog 생성 (builder)
        PracticeLog log = PracticeLog
                .builder()
                .member(member)
                .classes(classes)
                .expressionlessPhoto(request.getExpressionlessPhoto())
                .feedBack(request.getFeedBack())
                .count(request.getCount())
                .build();

        // 저장
        practiceLogRepository.save(log);

        return practiceLogConverter.toPracticeLogId(log);

    }

    private Classes findClass(long classId) {
        return classRepository.findById(classId)
                .orElseThrow(() -> new BusinessException(CLASS_NOT_FOUND));
    }
}
