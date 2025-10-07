package com.unithon.aeio.domain.review.repository;

import com.unithon.aeio.domain.review.dto.ReviewResponse;
import com.unithon.aeio.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    /**
     * 클래스 ID로 리뷰 페이지 조회.
     * Review -> MemberClass(mc) -> Member(m), Classes(c)
     */
    @EntityGraph(attributePaths = {"memberClass", "memberClass.member"})
    Page<Review> findByMemberClass_Classes_Id(Long classId, Pageable pageable);
}