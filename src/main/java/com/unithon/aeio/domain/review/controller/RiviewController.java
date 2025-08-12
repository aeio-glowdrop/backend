package com.unithon.aeio.domain.review.controller;

import com.unithon.aeio.domain.classes.dto.ClassRequest;
import com.unithon.aeio.domain.classes.dto.ClassResponse;
import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.domain.review.dto.ReviewRequest;
import com.unithon.aeio.domain.review.dto.ReviewResponse;
import com.unithon.aeio.domain.review.service.ReviewService;
import com.unithon.aeio.global.result.ResultResponse;
import com.unithon.aeio.global.result.code.ClassResultCode;
import com.unithon.aeio.global.security.annotation.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.unithon.aeio.global.result.code.ReviewResultCode.CREATE_REVIEW;
import static com.unithon.aeio.global.result.code.ReviewResultCode.DELETE_REVIEW;

@RestController
@RequestMapping("/review")
@Tag(name = "04. 리뷰 API", description = "리뷰 도메인의 API입니다.")
@RequiredArgsConstructor
public class RiviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "리뷰 작성 API", description = "새로운 리뷰를 생성하는 API입니다.")
    public ResultResponse<ReviewResponse.ReviewId> createReview(@RequestBody @Valid ReviewRequest.ReviewInfo request,
                                                                @RequestParam("classId") Long classId,
                                                                @LoginMember Member member) {
        return ResultResponse.of(CREATE_REVIEW,
                reviewService.createReview(request, classId, member));
    }

    @DeleteMapping
    @Operation(summary = "리뷰 삭제 API", description = "리뷰 및 연결된 리뷰포토를 소프트 삭제합니다.")
    public ResultResponse<ReviewResponse.DeleteReview> deleteReview(@RequestParam("reviewId") Long reviewId,
                                                                    @LoginMember Member member) {
        return ResultResponse.of(DELETE_REVIEW,
                reviewService.deleteReview(reviewId, member));
    }
}
