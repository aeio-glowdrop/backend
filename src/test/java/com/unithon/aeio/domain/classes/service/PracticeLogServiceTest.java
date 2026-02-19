package com.unithon.aeio.domain.classes.service;

import com.unithon.aeio.domain.classes.converter.PracticeLogConverter;
import com.unithon.aeio.domain.classes.dto.PracticeLogRequest;
import com.unithon.aeio.domain.classes.dto.PracticeLogResponse;
import com.unithon.aeio.domain.classes.entity.Classes;
import com.unithon.aeio.domain.classes.entity.MemberClass;
import com.unithon.aeio.domain.classes.repository.ClassRepository;
import com.unithon.aeio.domain.classes.repository.MemberClassRepository;
import com.unithon.aeio.domain.classes.repository.PracticeLogRepository;
import com.unithon.aeio.domain.member.entity.Member;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PracticeLogServiceTest {

    @Mock private ClassRepository classRepository;
    @Mock private PracticeLogRepository practiceLogRepository;
    @Mock private MemberClassRepository memberClassRepository;
    @Spy  private PracticeLogConverter practiceLogConverter;
    @Mock private com.amazonaws.services.s3.AmazonS3 amazonS3;

    @InjectMocks
    private PracticeLogServiceImpl practiceLogService;

    private Member member;
    private Classes classes;
    private MemberClass memberClass;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .authId("test-auth-id")
                .build();

        classes = Classes.builder()
                .id(10L)
                .className("얼굴 요가 A")
                .build();

        memberClass = MemberClass.builder()
                .id(100L)
                .member(member)
                .classes(classes)
                .totalCount(0)
                .build();
    }

    @Test
    @DisplayName("운동 기록 저장 시 totalCount가 1 증가한다")
    void createPracticeLog_incrementsTotalCount() {
        // Arrange
        PracticeLogRequest.BasicLog request = PracticeLogRequest.BasicLog.builder()
                .expressionlessPhoto("https://s3.example.com/photo/before.jpg")
                .practicePhoto("https://s3.example.com/photo/after.jpg")
                .feedBack("좋았어요")
                .count(10)
                .build();

        when(classRepository.findById(10L)).thenReturn(Optional.of(classes));
        when(memberClassRepository.findByMemberIdAndClassesId(1L, 10L)).thenReturn(Optional.of(memberClass));
        when(practiceLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        int before = memberClass.getTotalCount(); // 0

        // Act
        practiceLogService.createPracticeLog(10L, member, request);

        // Assert
        assertThat(memberClass.getTotalCount()).isEqualTo(before + 1);
    }

    @Test
    @DisplayName("운동 3회 기록 시 totalCount가 3이 된다")
    void createPracticeLog_thriceThenTotalCountIsThree() {
        PracticeLogRequest.BasicLog request = PracticeLogRequest.BasicLog.builder()
                .expressionlessPhoto("https://s3.example.com/photo/before.jpg")
                .practicePhoto("https://s3.example.com/photo/after.jpg")
                .feedBack("좋았어요")
                .count(10)
                .build();

        when(classRepository.findById(10L)).thenReturn(Optional.of(classes));
        when(memberClassRepository.findByMemberIdAndClassesId(1L, 10L)).thenReturn(Optional.of(memberClass));
        when(practiceLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        practiceLogService.createPracticeLog(10L, member, request);
        practiceLogService.createPracticeLog(10L, member, request);
        practiceLogService.createPracticeLog(10L, member, request);

        // Assert
        assertThat(memberClass.getTotalCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("getTotalCount는 현재 memberClass의 totalCount를 반환한다")
    void getTotalCount_returnsCurrentCount() {
        // Arrange
        memberClass.setTotalCount(5);
        when(memberClassRepository.findByMemberIdAndClassesId(1L, 10L)).thenReturn(Optional.of(memberClass));

        // Act
        PracticeLogResponse.TotalCount result = practiceLogService.getTotalCount(10L, member);

        // Assert
        assertThat(result.getTotalCount()).isEqualTo(5);
        assertThat(result.getMemberClassId()).isEqualTo(100L);
        assertThat(result.getClassId()).isEqualTo(10L);
    }
}
