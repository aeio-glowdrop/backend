package com.unithon.aeio.domain.classes.service;

import com.unithon.aeio.domain.classes.converter.ClassConverter;
import com.unithon.aeio.domain.classes.dto.ClassRequest;
import com.unithon.aeio.domain.classes.dto.ClassResponse;
import com.unithon.aeio.domain.classes.entity.Classes;
import com.unithon.aeio.domain.classes.entity.MemberClass;
import com.unithon.aeio.domain.classes.repository.ClassRepository;
import com.unithon.aeio.domain.classes.repository.MemberClassRepository;
import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.unithon.aeio.global.error.code.ClassErrorCode.ALREADY_SUBSCRIBED;
import static com.unithon.aeio.global.error.code.ClassErrorCode.CLASS_NOT_FOUND;


@Service
@Transactional
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;
    private final ClassConverter classConverter;
    private final MemberClassRepository memberClassRepository;

    @Override
    public ClassResponse.ClassId createClass(ClassRequest.ClassInfo request) {
        // 엔티티로 변환
        Classes classes = Classes
                .builder()
                .classType(request.getClassType())
                .teacher(request.getTeacher())
                .className(request.getClassName())
                .build();

        // 저장
        classRepository.save(classes);

        return classConverter.toClassId(classes);
    }

    @Override
    public ClassResponse.MemberClassId subsClass(Long classId, Member member) {
        // 클래스 조회
        Classes classes = classRepository.findById(classId)
                .orElseThrow(() -> new BusinessException(CLASS_NOT_FOUND));

        // 이미 구독 여부 확인 (재구독 시 에러 반환)
        if (memberClassRepository.existsByMemberIdAndClassesId(member.getId(), classId)) {
            throw new BusinessException(ALREADY_SUBSCRIBED);
        }

        // 구독 생성
        MemberClass mc = MemberClass
                .builder()
                .member(member)
                .classes(classes)
                .build();

        MemberClass saved = memberClassRepository.save(mc);

        return classConverter.toSubsClass(saved);
    }
}
