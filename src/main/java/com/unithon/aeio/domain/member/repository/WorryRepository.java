package com.unithon.aeio.domain.member.repository;

import com.unithon.aeio.domain.member.entity.Worry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorryRepository extends JpaRepository<Worry, Long> {
    List<Worry> findAllByMemberId(Long memberId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Worry w where w.member.id = :memberId")
    void deleteByMemberId(@Param("memberId") Long memberId);

    @Query("""
        select w.name
        from Worry w
        where w.member.id = :memberId
    """)
    List<String> findWorryNamesByMemberId(Long memberId);
}

