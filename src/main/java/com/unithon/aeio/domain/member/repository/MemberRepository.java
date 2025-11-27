package com.unithon.aeio.domain.member.repository;

import com.unithon.aeio.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByAuthId(String authId);
    Optional<Member> findByRefreshToken(String refreshToken);
    Boolean existsByAuthId(String authId);
}
