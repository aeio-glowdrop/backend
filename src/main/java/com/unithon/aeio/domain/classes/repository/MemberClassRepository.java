package com.unithon.aeio.domain.classes.repository;

import com.unithon.aeio.domain.classes.entity.MemberClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberClassRepository extends JpaRepository<MemberClass, Long> {

    Optional<MemberClass> findByMemberIdAndClassesId(Long memberId, Long classesId);
    boolean existsByMemberIdAndClassesId(Long memberId, Long classesId);
}
