package com.unithon.aeio.domain.member.service;

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

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberConverter memberConverter;
    private final WorryRepository worryRepository;

    @Override
    public MemberResponse.MemberId createMember(MemberRequest.MemberInfo request, Member loginMember) {
        // 로그인 없을 때, 임시로 멤버 생성
        Member member = getOrCreateMember(loginMember);
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

    // 멤버 찾고 없으면 저장하는 함수 (임시 로그인 패스 대용)
    private Member getOrCreateMember(Member loginMember) {
        if (loginMember != null && loginMember.getId() != null) {
            return memberRepository.findById(loginMember.getId())
                    .orElseGet(() -> memberRepository.save(Member.builder().build()));
        }
        // loginMember 자체가 null이거나 id가 없으면 바로 생성
        return memberRepository.save(Member.builder().build());
    }
}
