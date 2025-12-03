package com.unithon.aeio.domain.classes.entity;

import com.unithon.aeio.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "practice_log")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PracticeLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "practice_log_id")
    private Long id;

    @Column
    private String expressionlessPhoto; //무표정 사진 URL
    @Column
    private String practicePhoto; //운동 중 사진 URL
    @Column
    private String feedBack;
    @Column
    private Integer count; //몇번 했는지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_class_id")
    private MemberClass memberClass;
}
