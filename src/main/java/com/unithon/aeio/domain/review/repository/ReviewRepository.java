package com.unithon.aeio.domain.review.repository;

import com.unithon.aeio.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    /**
     * 클래스 ID로 리뷰 페이지 조회.
     * Review -> MemberClass(mc) -> Member(m), Classes(c)
     */
    @EntityGraph(attributePaths = {"memberClass", "memberClass.member"})
    Page<Review> findByMemberClass_Classes_Id(Long classId, Pageable pageable);

    @Query("SELECT AVG(r.rate) FROM Review r WHERE r.memberClass.classes.id = :classId")
    Double findAverageRateByClassId(@Param("classId") Long classId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.memberClass.member.id = :memberId")
    long countByMemberId(@Param("memberId") Long memberId);

    /**
     * 멤버 ID로 내 리뷰 목록 조회.
     * Review -> MemberClass -> Classes 미리 로딩
     */
    @EntityGraph(attributePaths = {"memberClass", "memberClass.classes"})
    List<Review> findByMemberClass_Member_Id(Long memberId);
}