package com.unithon.aeio.domain.member.service;

import com.unithon.aeio.domain.classes.entity.Classes;
import com.unithon.aeio.domain.classes.repository.PracticeLogRepository;
import com.unithon.aeio.domain.member.converter.MemberConverter;
import com.unithon.aeio.domain.member.dto.MemberRequest;
import com.unithon.aeio.domain.member.dto.MemberResponse;
import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.domain.member.entity.Worry;
import com.unithon.aeio.domain.member.repository.MemberRepository;
import com.unithon.aeio.domain.member.repository.WorryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberConverter memberConverter;
    private final WorryRepository worryRepository;
    private final PracticeLogRepository practiceLogRepository;

    @Override
    public MemberResponse.MemberId createMember(MemberRequest.MemberInfo request, Member member) {
        // 프로필 업데이트
        member.setNickname(request.getNickName());
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
    public MemberResponse.NickName getNickName(Member member) {
        return memberConverter.toNickName(member);
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

        // 연관된 고민부위(Worry)는 hard delete
        worryRepository.deleteByMemberId(member.getId());

        // soft delete (연관된 memberClass, review 함께 삭제)
        member.delete();
        memberRepository.save(member);

        return memberConverter.toMemberId(member);
    }
}
