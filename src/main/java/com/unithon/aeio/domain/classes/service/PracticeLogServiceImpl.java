package com.unithon.aeio.domain.classes.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.unithon.aeio.domain.classes.converter.PracticeLogConverter;
import com.unithon.aeio.domain.classes.dto.PracticeLogRequest;
import com.unithon.aeio.domain.classes.dto.PracticeLogResponse;
import com.unithon.aeio.domain.classes.entity.Classes;
import com.unithon.aeio.domain.classes.entity.MemberClass;
import com.unithon.aeio.domain.classes.entity.PracticeLog;
import com.unithon.aeio.domain.classes.repository.ClassRepository;
import com.unithon.aeio.domain.classes.repository.MemberClassRepository;
import com.unithon.aeio.domain.classes.repository.PracticeLogRepository;
import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.unithon.aeio.global.error.code.ClassErrorCode.CLASS_NOT_FOUND;
import static com.unithon.aeio.global.error.code.ClassErrorCode.MEMBER_CLASS_NOT_FOUND;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PracticeLogServiceImpl implements PracticeLogService {

    private final AmazonS3 amazonS3;
    private final PracticeLogConverter practiceLogConverter;
    private final ClassRepository classRepository;
    private final PracticeLogRepository practiceLogRepository;
    private final MemberClassRepository memberClassRepository;

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

        return new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(getPreSignedUrlExpiration());
    }

    // PreSigned URL 유효 기간 설정
    private Date getPreSignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 3;
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    // 사진 조회용(GET) PreSigned URL 생성
    @Override
    public String generateGetPresignedUrlFromPhotoUrl(String photoUrl) {
        try {
            // photoUrl에서 key만 추출
            URI uri = URI.create(photoUrl);
            String key = uri.getPath().substring(1); // 맨 앞 '/' 제거

            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(BUCKET_NAME, key)
                    .withMethod(HttpMethod.GET) // GET (조회용)
                    .withExpiration(getPreSignedUrlExpirationForGet()); // 3분짜리든, 따로 duration 만들어도 됨

            URL url = amazonS3.generatePresignedUrl(request);
            return url.toString();
        } catch (Exception e) {
            log.error("[S3] GET presigned URL 생성 실패. photoUrl={}", photoUrl, e);
            throw new RuntimeException("S3 Presigned URL 생성 실패");
        }
    }

    // Get용 PreSigned URL 유효 기간 설정
    private Date getPreSignedUrlExpirationForGet() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60; // 1시간
        expiration.setTime(expTimeMillis);
        return expiration;
    }


    // ---------- 여기까지 presigned

    @Override
    public PracticeLogResponse.PracticeLogId createPracticeLog(Long classId, Member member, PracticeLogRequest.BasicLog request) {
        // 클래스 존재 확인
        Classes classes = findClass(classId);

        // 구독 정보 가져오기
        MemberClass memberClass = findMemberClass(member.getId(), classId);

        // practiceLog 생성 (builder)
        PracticeLog log = PracticeLog
                .builder()
                .memberClass(memberClass)
                .expressionlessPhoto(request.getExpressionlessPhoto())
                .practicePhoto(request.getPracticePhoto())
                .feedBack(request.getFeedBack())
                .count(request.getCount())
                .build();

        // 저장
        practiceLogRepository.save(log);

        // 운동 1회 누적
        memberClass.setTotalCount(memberClass.getTotalCount() + 1);

        return practiceLogConverter.toPracticeLogId(log);

    }

    @Override
    public List<PracticeLogResponse.PracticeItem> getPracticeListByDate(LocalDate date, Member member) {
        // 날짜 시작 ~ 끝
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // 해당 멤버의 모든 practiceLog 중 날짜 범위 내의 것만 최신순으로 조회
        List<PracticeLog> logs = practiceLogRepository
                .findByMemberClassMemberAndCreatedAtBetweenOrderByCreatedAtDesc(member, startOfDay, endOfDay);

        return logs.stream()
                .map(practiceLogConverter::toItem)
                .toList();
    }

    // "운동한 날짜 리스트" 반환
    @Override
    public List<PracticeLogResponse.PracticeDate> getPracticeDateList(Member member) {
        List<LocalDate> dates = practiceLogRepository.findDistinctPracticeDatesByMember(member)
                .stream()
                .map(java.sql.Date::toLocalDate)
                .toList();

        return dates.stream()
                .map(PracticeLogResponse.PracticeDate::from)
                .toList();
    }

    @Override
    public PracticeLogResponse.TotalCount getTotalCount(Long classId, Member member) {
        MemberClass memberClass = findMemberClass(member.getId(), classId);
        return practiceLogConverter.toTotalCount(memberClass);
    }

    @Override
    public PracticeLogResponse.ClassStreak getClassStreak(Long classId, Member member) {
        List<LocalDate> dates = practiceLogRepository
                .findDistinctActivityDatesByMemberAndClass(member.getId(), classId);

        int streak = 0;

        // 오늘 기록이 없으면 스트릭 0
        LocalDate today = LocalDate.now();
        if (dates.isEmpty() || !dates.get(0).isEqual(today)) {
            return PracticeLogResponse.ClassStreak.builder()
                    .classId(classId)
                    .streak(streak)
                    .build();
        }

        // 오늘부터 하루씩 감소하며 연속성 체크
        LocalDate expected = today;
        for (LocalDate date : dates) {
            if (date.isEqual(expected)) {
                streak++;
                expected = expected.minusDays(1);
            } else if (date.isBefore(expected)) {
                break;
            }
        }

        return PracticeLogResponse.ClassStreak.builder()
                .classId(classId)
                .streak(streak)
                .build();
    }

    @Override
    public Classes findClass(long classId) {
        return classRepository.findById(classId)
                .orElseThrow(() -> new BusinessException(CLASS_NOT_FOUND));
    }

    @Override
    public MemberClass findMemberClass(long memberId, long classId) {
        return memberClassRepository.findByMemberIdAndClassesId(memberId, classId)
                .orElseThrow(() -> new BusinessException(MEMBER_CLASS_NOT_FOUND));
    }
}
