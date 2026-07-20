package com.unithon.aeio.domain.classes.controller;

import com.unithon.aeio.domain.classes.entity.Classes;
import com.unithon.aeio.domain.classes.repository.ClassRepository;
import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.domain.member.repository.MemberRepository;
import com.unithon.aeio.domain.member.service.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 실제 로컬 MySQL(application.yml 설정)에 대해 GET /classes/{classId}/info 를 검증한다.
 * @Transactional로 테스트 종료 시 삽입한 데이터를 자동 롤백한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClassControllerInfoTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ClassRepository classRepository;
    @Autowired private JwtTokenService jwtTokenService;

    @Test
    void 클래스_상세_조회_응답에_createdAt이_클래스_생성시각으로_채워진다() throws Exception {
        // Arrange
        Member member = memberRepository.save(Member.builder()
                .authId("test-auth-id-class-info")
                .nickname("테스트유저")
                .build());
        String accessToken = jwtTokenService.createAccessToken(member.getAuthId());

        Classes classes = classRepository.save(Classes.builder()
                .className("표정 근육 이완 클래스")
                .teacher("김강사")
                .thumbnailUrl("https://aeio-photo2.s3.ap-northeast-2.amazonaws.com/photo/thumbnail-sample.jpg")
                .time(10)
                .build());

        // Act & Assert
        mockMvc.perform(get("/classes/{classId}/info", classes.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.classId").value(classes.getId()))
                .andExpect(jsonPath("$.data.createdAt").value(notNullValue()))
                // subscribedAt은 구독 목록 조회 전용 필드이므로 클래스 상세 조회에서는 항상 비어있어야 한다
                .andExpect(jsonPath("$.data.subscribedAt").doesNotExist());
    }
}
