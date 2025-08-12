package com.unithon.aeio.domain.classes.converter;

import com.unithon.aeio.domain.classes.dto.ClassResponse;
import com.unithon.aeio.domain.classes.entity.ClassLike;
import com.unithon.aeio.domain.classes.entity.Classes;
import com.unithon.aeio.domain.classes.entity.MemberClass;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClassConverter {

    // class Id만 반환
    public ClassResponse.ClassId toClassId(Classes classes) {
        return ClassResponse.ClassId
                .builder()
                .classId(classes.getId())
                .build();
    }

    // memberClass id만 반환
    public ClassResponse.MemberClassId toSubs(MemberClass mc) {
        return ClassResponse.MemberClassId
                .builder()
                .memberClassId(mc.getId())
                .build();
    }

    // classLike id만 반환
    public ClassResponse.LikeInfo toClassLikeId(ClassLike cl) {
        return ClassResponse.LikeInfo
                .builder()
                .classLikeId(cl.getId())
                .build();
    }

    public ClassResponse.SubsClass toSubsClass(MemberClass mc) {
        Classes c = mc.getClasses();
        return ClassResponse.SubsClass
                .builder()
                .classId(c.getId())
                .className(c.getClassName())
                .classType(c.getClassType())
                .teacher(c.getTeacher())
                .thumbnailUrl(c.getThumbnailUrl())
                .subscribedAt(mc.getCreatedAt()) // 구독 시각
                .build();
    }

    public ClassResponse.SubsList toSubsList(List<ClassResponse.SubsClass> items) {
        return ClassResponse.SubsList
                .builder()
                .subsList(items)
                .count(items.size())
                .build();
    }
}
