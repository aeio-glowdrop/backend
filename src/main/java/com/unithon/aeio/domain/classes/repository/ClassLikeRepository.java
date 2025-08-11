package com.unithon.aeio.domain.classes.repository;

import com.unithon.aeio.domain.classes.entity.ClassLike;
import com.unithon.aeio.domain.classes.entity.Classes;
import com.unithon.aeio.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassLikeRepository extends JpaRepository<ClassLike, Long> {
    Optional<ClassLike> findByClassesAndMember(Classes classes, Member member);
}
