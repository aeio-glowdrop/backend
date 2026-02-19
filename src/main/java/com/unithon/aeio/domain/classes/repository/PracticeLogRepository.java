package com.unithon.aeio.domain.classes.repository;

import com.unithon.aeio.domain.classes.entity.PracticeLog;
import com.unithon.aeio.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PracticeLogRepository extends JpaRepository<PracticeLog, Long> {

    @Query(value = """
        SELECT DATE(pl.created_at) AS activity_date
        FROM practice_log pl
        JOIN member_class mc ON pl.member_class_id = mc.member_class_id
        WHERE mc.member_id = :memberId
          AND pl.deleted_at IS NULL
          AND mc.deleted_at IS NULL
        GROUP BY activity_date
        ORDER BY activity_date DESC
        """, nativeQuery = true)
    List<Date> findDistinctActivityDatesDescRaw(@Param("memberId") Long memberId);

    // 편의 디폴트 메소드: LocalDate로 변환
    default List<LocalDate> findDistinctActivityDatesDesc(Long memberId) {
        return findDistinctActivityDatesDescRaw(memberId).stream()
                .map(java.sql.Date::toLocalDate)
                .toList();
    }

    // member 기준 + 날짜 범위 + 최신순
    List<PracticeLog> findByMemberClassMemberAndCreatedAtBetweenOrderByCreatedAtDesc(
            Member member,
            LocalDateTime startInclusive,
            LocalDateTime endInclusive
    );

    // 해당 멤버가 운동한 "날짜"만 distinct로 조회
    @Query("""
        select distinct function('date', p.createdAt)
        from PracticeLog p
        where p.memberClass.member = :member
        order by function('date', p.createdAt) desc
    """)
    List<java.sql.Date> findDistinctPracticeDatesByMember(@Param("member") Member member);

    // 특정 멤버 + 특정 클래스 기준 날짜 distinct 조회 (최신순)
    @Query(value = """
        SELECT DATE(pl.created_at) AS activity_date
        FROM practice_log pl
        JOIN member_class mc ON pl.member_class_id = mc.member_class_id
        WHERE mc.member_id = :memberId
          AND mc.class_id  = :classId
          AND pl.deleted_at IS NULL
          AND mc.deleted_at IS NULL
        GROUP BY activity_date
        ORDER BY activity_date DESC
        """, nativeQuery = true)
    List<Date> findDistinctActivityDatesByMemberAndClassRaw(
            @Param("memberId") Long memberId,
            @Param("classId") Long classId);

    default List<LocalDate> findDistinctActivityDatesByMemberAndClass(Long memberId, Long classId) {
        return findDistinctActivityDatesByMemberAndClassRaw(memberId, classId).stream()
                .map(Date::toLocalDate)
                .toList();
    }
}
