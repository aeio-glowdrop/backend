package com.unithon.aeio.domain.member.repository;

import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.domain.member.entity.UserAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAgreementRepository extends JpaRepository<UserAgreement, Long> {
    Optional<UserAgreement> findByMember(Member member);
}
