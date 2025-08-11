package com.unithon.aeio.domain.classes.service;

import com.unithon.aeio.domain.classes.converter.ClassConverter;
import com.unithon.aeio.domain.classes.dto.ClassRequest;
import com.unithon.aeio.domain.classes.dto.ClassResponse;
import com.unithon.aeio.domain.classes.entity.ClassLike;
import com.unithon.aeio.domain.classes.entity.Classes;
import com.unithon.aeio.domain.classes.entity.MemberClass;
import com.unithon.aeio.domain.classes.repository.ClassLikeRepository;
import com.unithon.aeio.domain.classes.repository.ClassRepository;
import com.unithon.aeio.domain.classes.repository.MemberClassRepository;
import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.unithon.aeio.global.error.code.ClassErrorCode.ALREADY_LIKED;
import static com.unithon.aeio.global.error.code.ClassErrorCode.ALREADY_SUBSCRIBED;
import static com.unithon.aeio.global.error.code.ClassErrorCode.CLASS_NOT_FOUND;


@Service
@Transactional
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;
    private final ClassConverter classConverter;
    private final MemberClassRepository memberClassRepository;
    private final ClassLikeRepository classLikeRepository;

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

    @Override
    public ClassResponse.LikeInfo likeClass(Long classId, Member member) {

        // class 엔티티 조회
        Classes classes = findClass(classId);

        // member-class 조합으로 이미 좋아요 눌렀는지 확인하고, 이미 존재하면 exception
        classLikeRepository.findByClassesAndMember(classes, member)
                .ifPresent(existingLike -> {
                    throw new BusinessException(ALREADY_LIKED);
                });

        // 4. 새로운 Like 엔티티를 생성하고 저장
        ClassLike classLike = ClassLike
                .builder()
                .member(member)
                .classes(classes)
                .build();
        classLikeRepository.save(classLike);

        // 5. 컨버터를 사용해 응답 DTO로 변환 (photo Id만 반환)
        return classConverter.toClassLikeId(classLike);
    }

    private Classes findClass(Long classId) {
        return classRepository.findById(classId)
                .orElseThrow(() -> new BusinessException(CLASS_NOT_FOUND));
    }
}
