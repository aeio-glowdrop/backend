package com.unithon.aeio.domain.classes.service;

import com.unithon.aeio.domain.classes.converter.ClassConverter;
import com.unithon.aeio.domain.classes.dto.ClassResponse;
import com.unithon.aeio.domain.classes.entity.Classes;
import com.unithon.aeio.domain.classes.entity.MemberClass;
import com.unithon.aeio.domain.classes.repository.ClassLikeRepository;
import com.unithon.aeio.domain.classes.repository.ClassRepository;
import com.unithon.aeio.domain.classes.repository.MemberClassRepository;
import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.global.error.BusinessException;
import com.unithon.aeio.global.error.code.ClassErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassServiceTest {

    @Mock private ClassRepository classRepository;
    @Spy  private ClassConverter classConverter;
    @Mock private MemberClassRepository memberClassRepository;
    @Mock private ClassLikeRepository classLikeRepository;
    @Mock private PracticeLogService practiceLogService;

    @InjectMocks
    private ClassServiceImpl classService;

    private Member member;
    private Classes classes;
    private MemberClass memberClass;

    @BeforeEach
    void setUp() {
        member = Member.builder().id(1L).authId("test-auth-id").build();
        classes = Classes.builder().id(10L).className("얼굴 요가 A").build();
        memberClass = MemberClass.builder()
                .id(100L)
                .member(member)
                .classes(classes)
                .totalCount(3)
                .build();
    }

    @Test
    @DisplayName("구독 취소 시 hard delete가 아니라 soft delete(deletedAt 설정)로 처리한다")
    void unsubsClass_softDeletesInsteadOfHardDelete() {
        when(memberClassRepository.findByMemberIdAndClassesIdAndDeletedAtIsNull(1L, 10L))
                .thenReturn(Optional.of(memberClass));

        classService.unsubsClass(10L, member);

        assertThat(memberClass.isDeleted()).isTrue();
        verify(memberClassRepository, never()).delete(memberClass);
    }

    @Test
    @DisplayName("구독 취소 후 재구독하면 기존 row를 재활성화하여 운동 기록/리뷰 이력을 보존한다")
    void subsClass_reactivatesSoftDeletedRow() {
        memberClass.softDelete();
        when(classRepository.findById(10L)).thenReturn(Optional.of(classes));
        when(memberClassRepository.findByMemberIdAndClassesId(1L, 10L))
                .thenReturn(Optional.of(memberClass));

        ClassResponse.MemberClassId result = classService.subsClass(10L, member);

        assertThat(result.getMemberClassId()).isEqualTo(100L);
        assertThat(memberClass.isDeleted()).isFalse();
        assertThat(memberClass.getTotalCount()).isEqualTo(3); // 기존 이력 보존
        verify(memberClassRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("이미 활성 구독 중인 클래스를 다시 구독하면 예외를 던진다")
    void subsClass_activeSubscription_throwsAlreadySubscribed() {
        when(classRepository.findById(10L)).thenReturn(Optional.of(classes));
        when(memberClassRepository.findByMemberIdAndClassesId(1L, 10L))
                .thenReturn(Optional.of(memberClass));

        assertThatThrownBy(() -> classService.subsClass(10L, member))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ClassErrorCode.ALREADY_SUBSCRIBED);
    }

    @Test
    @DisplayName("처음 구독하는 클래스는 새 MemberClass row를 생성한다")
    void subsClass_noPriorHistory_createsNew() {
        when(classRepository.findById(10L)).thenReturn(Optional.of(classes));
        when(memberClassRepository.findByMemberIdAndClassesId(1L, 10L))
                .thenReturn(Optional.empty());
        when(memberClassRepository.save(org.mockito.ArgumentMatchers.any()))
                .thenAnswer(inv -> inv.getArgument(0));

        ClassResponse.MemberClassId result = classService.subsClass(10L, member);

        assertThat(result).isNotNull();
        verify(memberClassRepository).save(org.mockito.ArgumentMatchers.any());
    }
}
