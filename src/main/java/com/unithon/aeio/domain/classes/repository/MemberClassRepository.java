package com.unithon.aeio.domain.classes.repository;

import com.unithon.aeio.domain.classes.entity.MemberClass;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberClassRepository extends JpaRepository<MemberClass, Long> {

    Optional<MemberClass> findByMemberIdAndClassesId(Long memberId, Long classesId);
    boolean existsByMemberIdAndClassesId(Long memberId, Long classesId);
    @EntityGraph(attributePaths = "classes") // N+1 방지
    List<MemberClass> findAllByMemberIdOrderByIdDesc(Long memberId);
    long countByClassesId(Long classId);
}
