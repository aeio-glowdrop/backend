package com.unithon.aeio.domain.member.repository;

import com.unithon.aeio.domain.member.entity.Worry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorryRepository extends JpaRepository<Worry, Long> {
    List<Worry> findAllByMemberId(Long memberId);
}

