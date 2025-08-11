package com.unithon.aeio.domain.classes.repository;

import com.unithon.aeio.domain.classes.entity.PracticeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PracticeLogRepository extends JpaRepository<PracticeLog, Long> {
}
