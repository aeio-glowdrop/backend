package com.unithon.aeio.domain.member.entity;

import com.unithon.aeio.domain.classes.entity.ClassLike;
import com.unithon.aeio.domain.classes.entity.MemberClass;
import com.unithon.aeio.domain.classes.entity.PracticeLog;
import com.unithon.aeio.domain.review.entity.Review;
import com.unithon.aeio.global.entity.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
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
@Table(name = "member")
@SQLRestriction("deleted_at is NULL")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    @Column
    private String authId;
    @Column
    private String name;
    @Column
    private String email;      // Access Token
    @Column
    private String refreshToken;     // Refresh Token
    @Column
    private String accessToken;      // Access Token

    @Column
    @Size(max = 9)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column
    private Gender gender;

    @OneToMany(mappedBy = "member")
    @Builder.Default
    private List<Worry> worries = new ArrayList<>();
    @OneToMany(mappedBy = "member")
    @Builder.Default
    private List<MemberClass> memberClassList = new ArrayList<>();
    @OneToMany(mappedBy = "member")
    @Builder.Default
    private List<ClassLike> classLikeList = new ArrayList<>();

    public void delete() {
        for (MemberClass memberClass : memberClassList) {
            memberClass.delete();
        }
        for (ClassLike classLike : classLikeList) {
            classLike.delete();
        }
        super.delete();
    }
}
