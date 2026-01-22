package com.unithon.aeio.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public abstract class ReviewResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewId {
        private Long reviewId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteReview {
        private Long ownerId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PagedReviewList {
        private Long classId;
        private List<ReviewInfo> reviews;
        private int page;
        private long totalElements;
        private boolean isFirst;
        private boolean isLast;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewInfo {
        private Long reviewId;
        private Double rate;
        private String text;
        private List<String> photoUrls;
        private LocalDateTime createdAt;
        private Long writerMemberId;
        private String writerNickname;
        private String writerProfileImage;
    }

}
