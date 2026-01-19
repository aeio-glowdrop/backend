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
    public static class ClassInfo {
        private Long classId;
        private String className;
        private String thumbnailUrl;
        private ClassType classType;
        private String teacher;
        private String level;
        private String focus1;
        private String focus2;
        private String focus3;
        private int time;
        private long subNum; //구독 인원수
        private LocalDateTime subscribedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubsList {
        private List<ClassInfo> subsList;
        private int count;
    }

    //좋아요한 리스트 페이징 조회
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PagedLikeList {
        private Long classId;
        private List<ClassInfo> likeClassList; //좋아요 누른 클래스 리스트
        private int page; // 페이지 번호
        private long totalElements; // 해당 조건에 부합하는 요소의 총 개수
        private boolean isFirst; // 첫 페이지 여부
        private boolean isLast; // 마지막 페이지 여부
    }
}
