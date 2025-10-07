package com.unithon.aeio.domain.review.repository;

import com.unithon.aeio.domain.review.entity.ReviewPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewPhotoRepository extends JpaRepository<ReviewPhoto, Long> {
    List<ReviewPhoto> findByReview_IdIn(List<Long> reviewIds);
}
