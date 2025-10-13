package com.unithon.aeio.domain.classes.entity;

import com.unithon.aeio.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "classes")
@SQLRestriction("deleted_at is NULL")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Classes extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private Long id;
    @Column(name = "class_type")
    private ClassType classType;
    @Column(nullable = false)
    private String className;
    @Column(nullable = false)
    private String teacher;
    @Column
    private String thumbnailUrl;

    @OneToMany(mappedBy = "classes")
    @Builder.Default
    private List<MemberClass> memberClassList = new ArrayList<>();
    @OneToMany(mappedBy = "classes")
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
