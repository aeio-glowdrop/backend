package com.unithon.aeio.domain.classes.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
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
        private LocalDateTime subscribedAt; // 구독 시각 (구독 목록 조회 시에만 사용, 값이 없으면 응답에서 생략됨)
        private LocalDateTime createdAt; // 클래스 생성 시각 (클래스 상세 조회 시에만 사용, 값이 없으면 응답에서 생략됨)
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubsList {
        private List<ClassInfo> subsList;
        private int count;
    }

    //좋아요한 클래스 목록 조회용 요약 정보
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeClassInfo {
        private Long classId;
        private String className;
        private String thumbnailUrl;
        private LocalDateTime subscribedAt; // 좋아요 누른 시각
    }

    //좋아요한 리스트 페이징 조회
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PagedLikeList {
        private List<LikeClassInfo> likeClassList; //좋아요 누른 클래스 리스트
        private int page; // 페이지 번호
        private long totalElements; // 해당 조건에 부합하는 요소의 총 개수
        private boolean isFirst; // 첫 페이지 여부
        private boolean isLast; // 마지막 페이지 여부
    }

    //좋아요한 리스트 전체 조회 (페이징 없음)
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeList {
        private List<LikeClassInfo> likeClassList;
        private int count;
    }
}
