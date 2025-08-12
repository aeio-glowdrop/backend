package com.unithon.aeio.domain.classes.dto;

import com.unithon.aeio.domain.classes.entity.ClassType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public abstract class ClassResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassId {
        private Long classId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberClassId {
        private Long memberClassId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeInfo {
        private Long classLikeId; // 좋아요 누른 시각
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubsClass {
        private Long classId;
        private String className;
        private String thumbnailUrl;
        private ClassType classType;
        private String teacher;
        private LocalDateTime subscribedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubsList {
        private List<SubsClass> subsList;
        private int count;
    }

}
