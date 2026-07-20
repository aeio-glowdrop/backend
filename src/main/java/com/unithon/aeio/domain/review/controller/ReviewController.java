package com.unithon.aeio.domain.review.controller;

import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.domain.review.converter.ReviewConverter;
import com.unithon.aeio.domain.review.dto.ReviewRequest;
import com.unithon.aeio.domain.review.dto.ReviewResponse;
import com.unithon.aeio.domain.review.service.ReviewService;
import com.unithon.aeio.global.result.ResultResponse;
import com.unithon.aeio.global.result.code.ReviewResultCode;
import com.unithon.aeio.global.security.annotation.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.unithon.aeio.global.result.code.ReviewResultCode.CREATE_REVIEW;
import static com.unithon.aeio.global.result.code.ReviewResultCode.DELETE_REVIEW;
import static com.unithon.aeio.global.result.code.ReviewResultCode.GET_MY_REVIEWS;

@RestController
@RequestMapping("/review")
@Tag(name = "04. 리뷰 API", description = "리뷰 도메인의 API입니다.")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewConverter reviewConverter;

    @PostMapping
    @Operation(summary = "리뷰 작성 API", description = "클래스에 대한 별점/텍스트/사진 리뷰를 작성합니다. 작성 시점의 누적운동횟수/스트릭이 함께 저장됩니다.")
    public ResultResponse<ReviewResponse.ReviewId> createReview(@RequestBody @Valid ReviewRequest.ReviewInfo request,
                                                                @RequestParam("classId") Long classId,
                                                                @LoginMember Member member) {
        return ResultResponse.of(CREATE_REVIEW,
                reviewService.createReview(request, classId, member));
    }

    @DeleteMapping
    @Operation(summary = "리뷰 삭제 API", description = "본인이 작성한 리뷰를 삭제합니다.")
    public ResultResponse<ReviewResponse.DeleteReview> deleteReview(@RequestParam("reviewId") Long reviewId,
                                                                    @LoginMember Member member) {
        return ResultResponse.of(DELETE_REVIEW,
                reviewService.deleteReview(reviewId, member));
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "리뷰 상세 조회 API", description = "리뷰 상세 정보를 조회합니다(인증 불필요, 비로그인도 조회 가능)")
    @Parameters({
            @Parameter(name = "reviewId", description = "조회할 리뷰 ID")
    })
    public ResultResponse<ReviewResponse.ReviewInfo> getReview(@PathVariable Long reviewId) {
        return ResultResponse.of(ReviewResultCode.REVIEW_INFO,
                reviewService.getReview(reviewId));
    }

    @GetMapping("/my")
    @Operation(summary = "내가 작성한 리뷰 목록 조회 API", description = "로그인 사용자가 작성한 모든 리뷰를 전체 조회합니다. (페이징 없음)")
    public ResultResponse<List<ReviewResponse.ReviewInfo>> getMyReviews(@LoginMember Member member) {
        return ResultResponse.of(GET_MY_REVIEWS, reviewService.getMyReviews(member));
    }

    @GetMapping("/{classId}/reviews")
    @Operation(summary = "클래스별 리뷰 목록 조회 API", description = "특정 클래스의 리뷰를 최신순 페이징 + 평균 별점과 함께 조회합니다(인증 불필요)")
    @Parameters({
            @Parameter(name = "page", description = "0부터 시작"),
            @Parameter(name = "size", description = "페이지 크기")
    })
    public ResultResponse<ReviewResponse.PagedReviewList> getClassReviews(
            @PathVariable Long classId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable
    ) {
        Page<ReviewResponse.ReviewInfo> page = reviewService.getClassReviewPage(classId, pageable);
        Double averageRate = reviewService.getAverageRateByClassId(classId);
        return ResultResponse.of(ReviewResultCode.REVIEW_LIST,
                reviewConverter.toPagedReviewList(classId, averageRate, page));
    }
}
