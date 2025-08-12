package com.unithon.aeio.domain.review.converter;

import com.unithon.aeio.domain.review.dto.ReviewResponse;
import com.unithon.aeio.domain.review.entity.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewConverter {
    // class Id만 반환
    public ReviewResponse.ReviewId toReviewId(Review review) {
        return ReviewResponse.ReviewId
                .builder()
                .reviewId(review.getId())
                .build();
    }
}
