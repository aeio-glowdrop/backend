package com.unithon.aeio.domain.review.service;

import com.unithon.aeio.domain.classes.entity.Classes;
import com.unithon.aeio.domain.classes.entity.MemberClass;
import com.unithon.aeio.domain.classes.service.PracticeLogService;
import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.domain.review.converter.ReviewConverter;
import com.unithon.aeio.domain.review.dto.ReviewResponse;
import com.unithon.aeio.domain.review.entity.Review;
import com.unithon.aeio.domain.review.repository.ReviewPhotoRepository;
import com.unithon.aeio.domain.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private PracticeLogService practiceLogService;
    @Mock private ReviewPhotoRepository reviewPhotoRepository;
    @Mock private ReviewConverter reviewConverter;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Member member;
    private Classes classes;
    private MemberClass memberClass;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .nickname("혜령")
                .build();

        classes = Classes.builder()
                .id(10L)
                .className("얼굴 요가 A")
                .build();

        memberClass = MemberClass.builder()
                .id(100L)
                .member(member)
                .classes(classes)
                .build();
    }

    @Test
    @DisplayName("getMyReviewPage는 페이징된 내 리뷰 목록을 반환한다")
    void getMyReviewPage_returnsPagedReviews() {
        Review review1 = Review.builder().id(1L).rate(4.5).text("좋아요").memberClass(memberClass).build();
        Review review2 = Review.builder().id(2L).rate(3.0).text("보통이에요").memberClass(memberClass).build();

        Pageable pageable = PageRequest.of(0, 10);
        when(reviewRepository.findByMemberClass_Member_Id(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(review1, review2), pageable, 2));
        when(reviewPhotoRepository.findByReview_IdIn(List.of(1L, 2L))).thenReturn(List.of());

        Page<ReviewResponse.ReviewInfo> result = reviewService.getMyReviewPage(member, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getReviewId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getClassId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("작성한 리뷰가 없으면 빈 페이지를 반환한다")
    void getMyReviewPage_noReviews_returnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(reviewRepository.findByMemberClass_Member_Id(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        Page<ReviewResponse.ReviewInfo> result = reviewService.getMyReviewPage(member, pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }
}
