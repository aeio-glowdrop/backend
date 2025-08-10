package com.unithon.aeio.domain.classes.service;

import com.unithon.aeio.domain.classes.controller.ClassController;
import com.unithon.aeio.domain.classes.converter.ClassConverter;
import com.unithon.aeio.domain.classes.dto.ClassRequest;
import com.unithon.aeio.domain.classes.dto.ClassResponse;
import com.unithon.aeio.domain.classes.entity.Classes;
import com.unithon.aeio.domain.classes.repository.ClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;
    private final ClassConverter converter;

    @Override
    public ClassResponse.ClassId createClass(ClassRequest.ClassInfo request) {
        // 엔티티로 변환
        Classes classes = Classes
                .builder()
                .classType(request.getClassType())
                .teacher(request.getTeacher())
                .build();

        // 저장
        classRepository.save(classes);

        return converter.toClassId(classes);
    }
}
