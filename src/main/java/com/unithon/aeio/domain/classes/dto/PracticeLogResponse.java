package com.unithon.aeio.domain.classes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public abstract class PracticeLogResponse {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PreSignedUrlList {
        private List<PreSignedUrl> preSignedUrlInfoList;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PreSignedUrl {
        private String preSignedUrl;
        private String photoUrl;
        private String photoName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PracticeLogId {
        private Long practiceLogId;
    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PracticeItem {
        private Long practiceLogId;
        private Long memberClassId;
        private Long classId;
        private String className;
        private String classType;
        private Integer count;  //수행 횟수
        private String feedback;
        private String expressionlessPhoto; // 무표정 사진 URL
        private String practicePhoto; // 연습중 사진 URL
        private LocalDateTime createdAt; // 기록 생성 시각
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PracticeItemList {
        private List<PracticeItem> practiceList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassStreak {
        private Long classId;
        private int streak;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalCount {
        private Long memberClassId;
        private Long classId;
        private Integer totalCount;
    }

    @Getter
    @AllArgsConstructor
    public static class PracticeDate {
        private LocalDate date;

        public static PracticeDate from(LocalDate date) {
            return new PracticeDate(date);
        }
    }
}
