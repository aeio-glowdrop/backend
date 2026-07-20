package com.unithon.aeio.domain.classes.repository;

import com.unithon.aeio.domain.classes.entity.MemberClass;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberClassRepository extends JpaRepository<MemberClass, Long> {

    // soft-delete 여부 무관 조회 (재구독 시 과거 row 존재 확인용)
    Optional<MemberClass> findByMemberIdAndClassesId(Long memberId, Long classesId);
    // 활성 구독만 조회 (unsubscribe 대상 확인, practice log/review 생성 시 구독 확인용)
    Optional<MemberClass> findByMemberIdAndClassesIdAndDeletedAtIsNull(Long memberId, Long classesId);
    boolean existsByMemberIdAndClassesIdAndDeletedAtIsNull(Long memberId, Long classesId);
    @EntityGraph(attributePaths = "classes") // N+1 방지
    List<MemberClass> findAllByMemberIdAndDeletedAtIsNullOrderByIdDesc(Long memberId);
    long countByClassesIdAndDeletedAtIsNull(Long classId);
    long countByMemberIdAndDeletedAtIsNull(Long memberId);
}
