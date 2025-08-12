package com.unithon.aeio.domain.classes.repository;

import com.unithon.aeio.domain.classes.entity.PracticeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
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
}
