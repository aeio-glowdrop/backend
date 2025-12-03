package com.unithon.aeio.domain.review.converter;

import com.unithon.aeio.domain.review.dto.ReviewResponse;
import com.unithon.aeio.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReviewConverter {
    // class Id만 반환
    public ReviewResponse.ReviewId toReviewId(Review review) {
        return ReviewResponse.ReviewId
                .builder()
                .reviewId(review.getId())
                .build();
    }

    public ReviewResponse.DeleteReview toDeleteReview(Long ownerId) {
        return ReviewResponse.DeleteReview.builder()
                .ownerId(ownerId)
                .build();
    }

    public ReviewResponse.PagedReviewList toPagedReviewList(Long classId, Page<ReviewResponse.ReviewInfo> page) {
        return ReviewResponse.PagedReviewList.builder()
                .classId(classId)
                .reviews(page.getContent())
                .page(page.getNumber())
                .totalElements(page.getTotalElements())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }
}
