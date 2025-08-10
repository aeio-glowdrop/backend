package com.unithon.aeio.domain.practice.repository;

import com.unithon.aeio.domain.practice.entity.PracticeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PracticeLogRepository extends JpaRepository<PracticeLog, Long> {
}
