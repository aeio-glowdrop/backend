package com.unithon.aeio.domain.classes.repository;


import com.unithon.aeio.domain.classes.entity.Classes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends JpaRepository<Classes, Long> {

}
