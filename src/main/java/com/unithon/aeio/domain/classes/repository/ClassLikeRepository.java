package com.unithon.aeio.domain.classes.repository;

import com.unithon.aeio.domain.classes.dto.ClassResponse;
import com.unithon.aeio.domain.classes.entity.ClassLike;
import com.unithon.aeio.domain.classes.entity.Classes;
import com.unithon.aeio.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassLikeRepository extends JpaRepository<ClassLike, Long> {
    Optional<ClassLike> findByClassesAndMember(Classes classes, Member member);

    @Query("""
        select c
        from ClassLike cl
            join cl.classes c
        where cl.member.id = :memberId
        order by cl.createdAt desc
        """)
    Page<Classes> findLikedClassesByMemberId(@Param("memberId") Long memberId, Pageable pageable);
}
