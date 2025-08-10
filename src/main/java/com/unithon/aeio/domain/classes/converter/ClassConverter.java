package com.unithon.aeio.domain.classes.converter;

import com.unithon.aeio.domain.classes.dto.ClassResponse;
import com.unithon.aeio.domain.classes.entity.Classes;
import com.unithon.aeio.domain.classes.entity.MemberClass;
import org.springframework.stereotype.Component;

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
    public ClassResponse.MemberClassId toSubsClass(MemberClass mc) {
        return ClassResponse.MemberClassId
                .builder()
                .memberClassId(mc.getId())
                .build();
    }
}
