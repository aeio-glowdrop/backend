package com.unithon.aeio.domain.classes.entity;

import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.domain.review.entity.Review;
import com.unithon.aeio.global.entity.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member_class")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberClass extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_class_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private Classes classes;

    @OneToMany(mappedBy = "memberClass", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PracticeLog> practiceLogList = new ArrayList<>();
    @OneToMany(mappedBy = "memberClass", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviewList = new ArrayList<>();
}
