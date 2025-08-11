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
}
