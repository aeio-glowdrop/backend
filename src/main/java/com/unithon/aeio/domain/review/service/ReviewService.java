package com.unithon.aeio.domain.review.service;

import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.domain.review.dto.ReviewRequest;
import com.unithon.aeio.domain.review.dto.ReviewResponse;

public interface ReviewService {
    ReviewResponse.ReviewId createReview(ReviewRequest.ReviewInfo request, Long classId, Member member);
    ReviewResponse.DeleteReview deleteReview(Long reviewId, Member loginMember);
}
