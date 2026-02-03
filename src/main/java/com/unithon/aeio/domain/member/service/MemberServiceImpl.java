package com.unithon.aeio.domain.member.service;

import com.unithon.aeio.domain.classes.repository.PracticeLogRepository;
import com.unithon.aeio.domain.member.converter.MemberConverter;
import com.unithon.aeio.domain.member.dto.MemberRequest;
import com.unithon.aeio.domain.member.dto.MemberResponse;
import com.unithon.aeio.domain.member.dto.OauthRequest;
import com.unithon.aeio.domain.member.dto.OauthResponse;
import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.domain.member.entity.UserAgreement;
import com.unithon.aeio.domain.member.entity.Worry;
import com.unithon.aeio.domain.member.repository.MemberRepository;
import com.unithon.aeio.domain.member.repository.UserAgreementRepository;
import com.unithon.aeio.domain.member.repository.WorryRepository;
import com.unithon.aeio.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.unithon.aeio.global.error.code.GlobalErrorCode.INVALID_URL;
import static com.unithon.aeio.global.error.code.JwtErrorCode.NICKNAME_BLANK;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberConverter memberConverter;
    private final WorryRepository worryRepository;
    private final PracticeLogRepository practiceLogRepository;
    private final UserAgreementRepository userAgreementRepository;

    // AWS SDK v2 기준
    private final software.amazon.awssdk.services.s3.S3Client s3Client;

    private static final String BUCKET = "aeio-photo2";
    private static final String PHOTO_PREFIX = "photo/";
    private static final String DEFAULT_PREFIX = "default/";
    // s3 호스트
    private static final String ALLOWED_HOST = "aeio-photo2.s3.ap-northeast-2.amazonaws.com";

    @Override
    public MemberResponse.MemberId createMember(MemberRequest.MemberInfo request, Member member) {
        // 프로필 업데이트
        member.setNickname(request.getNickName());
        member.setProfileURL(request.getProfileURL());
        // 성별 업데이트
        member.setGender(request.getGender());
        // Member 저장
        memberRepository.save(member);

        // worryList 저장
        List<Worry> worrieList = request.getWorryList()
                .stream()
                .map(name -> Worry
                        .builder()
                        .name(name)
                        .member(member)      // FK 연관관계 세팅
                        .build())
                .toList();

        worryRepository.saveAll(worrieList); // worryList 저장

        return memberConverter.toMemberId(member);
    }

    @Override
    public MemberResponse.MemberInfo getMemberInfo(Member member) {

        return memberConverter.toNickName(member);
    }

    @Override
    public MemberResponse.MemberInfo updateNickName(Member member, String nickname) {

        // 이름 필드가 비어 있다면 오류
        if (nickname == null || nickname.isBlank()) {
            throw new BusinessException(NICKNAME_BLANK);
        }

        member.setNickname(nickname);
        Member saved = memberRepository.save(member);

        return memberConverter.toNickName(saved);
    }

    @Override
    public MemberResponse.Streak getStreak(Member member){
        // 서버 타임존 사용(필요시 Asia/Seoul 등으로 고정 가능)
        LocalDate today = LocalDate.now();

        // 회원이 수행한 모든 클래스 (practiceLog) 의 날짜별 기록(중복 제거) - 최신순
        List<LocalDate> activityDatesDesc =
                practiceLogRepository.findDistinctActivityDatesDesc(member.getId());

        int streak = 0;

        // 오늘 기록이 없으면 스트릭 0
        if (activityDatesDesc.isEmpty() || activityDatesDesc.get(0).isBefore(today) || !activityDatesDesc.get(0).isEqual(today)) {
            return memberConverter.toStreak(today, streak);
        }

        // 연속성 체크: 오늘부터 하루씩 감소해가며 존재 여부 확인
        LocalDate expected = today;
        for (LocalDate d : activityDatesDesc) {
            if (d.isEqual(expected)) {
                streak++;
                expected = expected.minusDays(1);
            } else if (d.isBefore(expected)) {
                // 연속이 끊기면 종료
                break;
            } else {
                // d가 expected보다 미래일 수는 없지만, 혹시 정렬/데이터 이슈 대비
                continue;
            }
        }

        return memberConverter.toStreak(today, streak);
    }

    @Override
    @Transactional
    public MemberResponse.MemberId updateMember(MemberRequest.UpdateMemberInfo request, Member member) {

        // 닉네임
        if (request.getNickName() != null) {
            String nick = request.getNickName().trim();
            member.setNickname(nick);
        }

        // 성별
        if (request.getGender() != null) {
            member.setGender(request.getGender());
        }

        // 고민부위 (전체 교체)
        if (request.getWorryList() != null) {
            List<String> list = request.getWorryList();

            // 기존 전부 삭제 후 새로 저장
            worryRepository.deleteByMemberId(member.getId());

            List<Worry> newWorry = list.stream()
                    .map(name -> Worry.builder()
                            .name(name)
                            .member(member)
                            .build())
                    .toList();

            worryRepository.saveAll(newWorry);
        }

        // 4. 멤버 저장
        memberRepository.save(member);

        return memberConverter.toMemberId(member);
    }

    @Override
    public MemberResponse.MemberId deleteMember(Member member) {

        // 연관된 고민부위(Worry) hard delete
        worryRepository.deleteByMemberId(member.getId());
        memberRepository.delete(member);

        return memberConverter.toMemberId(member);
    }

    @Override
    public OauthResponse.CheckMemberRegistration saveUserAgreements(Member member, OauthRequest.AgreementRequest request) {

        // @Valid 통과 이후

        // 마케팅 동의: null이면 false로 처리
        boolean marketingAgree = Boolean.TRUE.equals(request.getMarketingAgree());
        LocalDateTime now = LocalDateTime.now();

        UserAgreement userAgreement = userAgreementRepository.findByMember(member)
                .orElseGet(() -> UserAgreement.builder()
                        .member(member)
                        .build()
                );

        // 버전 저장
        userAgreement.setTermsVersion(request.getTermsVersion());
        userAgreement.setPrivacyVersion(request.getPrivacyVersion());

        // 동의 여부 (Boolean)
        userAgreement.setTermsAgree(request.getTermsAgree());
        userAgreement.setPrivacyAgree(request.getPrivacyAgree());
        userAgreement.setPersonalInfoAgree(request.getPersonalInfoAgree());
        userAgreement.setAgeOver14At(request.getAgeOver14());

        // 선택 동의 (null → false)
        userAgreement.setMarketingAgree(marketingAgree);

        // 동의 시각
        userAgreement.setAgreedAt(now);

        userAgreementRepository.save(userAgreement);

        // 응답은 항상 true 고정
        return new OauthResponse.CheckMemberRegistration(true);
    }


    //------ 프로필 사진 업로드 처리
    @Override
    public MemberResponse.MemberId updateProfile(Member member, String profileImageUrl) {

        String newUrl = profileImageUrl == null ? null : profileImageUrl.trim();

        //프로필 링크 유효성 점검
        if (newUrl == null || newUrl.isBlank()) {
            throw new BusinessException(INVALID_URL);
        }

        // 최소 url 형식 체크
        if (!(newUrl.startsWith("https://") || newUrl.startsWith("http://"))) {
            throw new BusinessException(INVALID_URL);
        }

        // 우리 S3 버킷의 URL만 허용 (외부 링크/다른 버킷 방지)
        validateS3Url(newUrl);

        // 기존 url 가져오기
        String oldUrl = member.getProfileURL();
        if (newUrl.equals(oldUrl)) {
            return memberConverter.toMemberId(member);
        }

        // 1) DB 업데이트 (없으면 등록, 있으면 교체)
        member.setProfileURL(newUrl);
        Member saved = memberRepository.save(member);

        // 2) 커밋 후 기존 이미지 삭제 시도 (photo/만 삭제)
        deleteOldImageAfterCommit(oldUrl);

        return memberConverter.toMemberId(member);
    }

    private void validateS3Url(String url) {
        try {
            URI uri = URI.create(url);
            String host = uri.getHost();
            String path = uri.getPath();

            if (host == null || path == null) {
                throw new BusinessException(INVALID_URL);
            }

            // host 검증하기
            if (!ALLOWED_HOST.equals(host)) {
                throw new BusinessException(INVALID_URL);
            }

            // key는 photo/ 또는 default/ 로 시작해야 함
            String key = path.startsWith("/") ? path.substring(1) : path;
            if (!(key.startsWith(PHOTO_PREFIX) || key.startsWith(DEFAULT_PREFIX))) {
                throw new BusinessException(INVALID_URL);
            }
        } catch (IllegalArgumentException e) {
            throw new BusinessException(INVALID_URL);
        }
    }

    private void deleteOldImageAfterCommit(String oldUrl) {
        if (oldUrl == null || oldUrl.isBlank()) return;

        ExtractedS3 old = extractS3FromAllowedUrl(oldUrl);
        if (old == null) return;

        //  오직 photo/만 삭제 (default/ 는 보호)
        if (!old.key().startsWith(PHOTO_PREFIX)) return;

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    s3Client.deleteObject(b -> b.bucket(old.bucket()).key(old.key()));
                } catch (Exception e) {
                    // 삭제 실패는 치명적이지 않음(쓰레기 객체가 남는 정도)
                    log.warn("Failed to delete old profile image. bucket={}, key={}, oldUrl={}",
                            old.bucket(), old.key(), oldUrl, e);
                }
            }
        });
    }

    // 최종 URL에서 bucket+key 추출
    private ExtractedS3 extractS3FromAllowedUrl(String url) {
        try {
            URI uri = URI.create(url);
            if (!ALLOWED_HOST.equals(uri.getHost())) return null;

            String path = uri.getPath();
            if (path == null || path.isBlank() || "/".equals(path)) return null;

            String key = path.startsWith("/") ? path.substring(1) : path;
            if (!(key.startsWith(PHOTO_PREFIX) || key.startsWith(DEFAULT_PREFIX))) return null;

            return new ExtractedS3(BUCKET, key);
        } catch (Exception e) {
            return null;
        }
    }

    private record ExtractedS3(String bucket, String key) {}
}
