package com.unithon.aeio.domain.review.service;

import com.unithon.aeio.domain.classes.entity.MemberClass;
import com.unithon.aeio.domain.classes.service.PracticeLogService;
import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.domain.review.converter.ReviewConverter;
import com.unithon.aeio.domain.review.dto.ReviewRequest;
import com.unithon.aeio.domain.review.dto.ReviewResponse;
import com.unithon.aeio.domain.review.entity.Review;
import com.unithon.aeio.domain.review.entity.ReviewPhoto;
import com.unithon.aeio.domain.review.repository.ReviewPhotoRepository;
import com.unithon.aeio.domain.review.repository.ReviewRepository;
import com.unithon.aeio.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.unithon.aeio.global.error.code.ReviewErrorCode.RATE_MUST_BE_HALF_STEP;
import static com.unithon.aeio.global.error.code.ReviewErrorCode.RATE_OUT_OF_RANGE;
import static com.unithon.aeio.global.error.code.ReviewErrorCode.RATE_REQUIRED;
import static com.unithon.aeio.global.error.code.ReviewErrorCode.REVIEW_AUTH_ORBIDDEN;
import static com.unithon.aeio.global.error.code.ReviewErrorCode.REVIEW_NOT_FOUND;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final PracticeLogService practiceLogService;
    private final ReviewPhotoRepository reviewPhotoRepository;
    private final ReviewConverter reviewConverter;

    @Override
    public ReviewResponse.ReviewId createReview(ReviewRequest.ReviewInfo req, Long classId,  Member member) {
        // 0) 별점 형식 검증 (0.0~5.0, 0.5 단위)
        validateRate(req.getRate());

        // 1) 클래스 id가 유효한지 확인
       practiceLogService.findClass(classId);

        // 2) 멤버-클래스 관계(MemberClass) 조회 & 유효한지 확인
        MemberClass memberClass = practiceLogService.findMemberClass(member.getId(), classId);

        // 3) 리뷰 엔티티 생성 및 저장
        Review review = Review
                .builder()
                .rate(req.getRate())
                .text(req.getReviewText())
                .memberClass(memberClass)   // Review -> MemberClass (N:1)
                .build();
        Review saved = reviewRepository.save(review);

        // 리뷰 사진 저장
        if (req.getPhotoUrl() != null && !req.getPhotoUrl().isEmpty()) {
            List<ReviewPhoto> photos = req.getPhotoUrl()
                    .stream()
                    .filter(org.springframework.util.StringUtils::hasText)
                    .map(url -> ReviewPhoto
                            .builder()
                            .review(saved)   // FK 세팅
                            .photoUrl(url)
                            .build())
                    .toList();

            if (!photos.isEmpty()) {
                reviewPhotoRepository.saveAll(photos);
            }
        }

        return reviewConverter.toReviewId(saved);
    }


    @Override
    public ReviewResponse.DeleteReview deleteReview(Long reviewId, Member loginMember) {

        // 리뷰 id 검증
        Review review = findReview(reviewId);


        // 이 리뷰가 로그인한 멤버의 것인지 확인
        Long ownerId = review.getMemberClass().getMember().getId();
        if (!ownerId.equals(loginMember.getId())) {
            throw new BusinessException(REVIEW_AUTH_ORBIDDEN);
        }

        // 자식 먼저 soft delete → 그 다음 본인 soft delete
        review.delete();

        // JPA dirty checking으로 반영됨
        return reviewConverter.toDeleteReview(review);
    }

    private void validateRate(Double rate) {
        if (rate == null) {
            throw new BusinessException(RATE_REQUIRED);
        }
        if (rate < 0.0 || rate > 5.0) {
            throw new BusinessException(RATE_OUT_OF_RANGE);
        }
        // 0.5 단위 검증
        double x2 = rate * 2.0;
        if (Math.abs(x2 - Math.round(x2)) > 1e-9) {
            throw new BusinessException(RATE_MUST_BE_HALF_STEP);
        }
    }

    private Review findReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(REVIEW_NOT_FOUND));
    }
}
