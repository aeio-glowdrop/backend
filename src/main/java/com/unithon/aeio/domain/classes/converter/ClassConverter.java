package com.unithon.aeio.domain.classes.converter;

import com.unithon.aeio.domain.classes.dto.ClassResponse;
import com.unithon.aeio.domain.classes.entity.ClassLike;
import com.unithon.aeio.domain.classes.entity.Classes;
import com.unithon.aeio.domain.classes.entity.MemberClass;
import org.springframework.data.domain.Page;
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

    public ClassResponse.ClassInfo toSubsClass(MemberClass mc) {
        Classes c = mc.getClasses();
        return ClassResponse.ClassInfo
                .builder()
                .classId(c.getId())
                .className(c.getClassName())
                .classType(c.getClassType())
                .teacher(c.getTeacher())
                .thumbnailUrl(c.getThumbnailUrl())
                .level(c.getLevel())
                .focus1(c.getFocus1())
                .focus2(c.getFocus2())
                .focus3(c.getFocus3())
                .time(c.getTime())
                .subscribedAt(mc.getCreatedAt()) // 구독 시각
                .build();
    }

    public ClassResponse.ClassInfo toClassInfo(Classes classes) {
        return ClassResponse.ClassInfo
                .builder()
                .classId(classes.getId())
                .className(classes.getClassName())
                .classType(classes.getClassType())
                .teacher(classes.getTeacher())
                .thumbnailUrl(classes.getThumbnailUrl())
                .level(classes.getLevel())
                .focus1(classes.getFocus1())
                .focus2(classes.getFocus2())
                .focus3(classes.getFocus3())
                .time(classes.getTime())
                .subscribedAt(classes.getCreatedAt()) // 구독 시각
                .build();
    }

    public ClassResponse.SubsList toSubsList(List<ClassResponse.ClassInfo> items) {
        return ClassResponse.SubsList
                .builder()
                .subsList(items)
                .count(items.size())
                .build();
    }

    public ClassResponse.PagedLikeList toPagedLikeList(Page<ClassResponse.ClassInfo> page) {
        return ClassResponse.PagedLikeList.builder()
                .likeClassList(page.getContent())
                .page(page.getNumber())
                .totalElements(page.getTotalElements())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }
}
