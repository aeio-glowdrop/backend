package com.unithon.aeio.domain.classes.controller;

import com.unithon.aeio.domain.classes.entity.ClassLike;
import com.unithon.aeio.domain.classes.entity.Classes;
import com.unithon.aeio.domain.classes.repository.ClassLikeRepository;
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
 * 실제 로컬 MySQL(application.yml 설정)에 대해 GET /classes/likes/me 를 검증한다.
 * @Transactional로 테스트 종료 시 삽입한 데이터를 자동 롤백한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClassControllerLikesMeTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ClassRepository classRepository;
    @Autowired private ClassLikeRepository classLikeRepository;
    @Autowired private JwtTokenService jwtTokenService;

    @Test
    void 좋아요한_클래스_목록_조회_응답을_검증한다() throws Exception {
        // Arrange: 회원, 클래스, 좋아요 데이터 준비
        Member member = memberRepository.save(Member.builder()
                .authId("test-auth-id-likes-me")
                .nickname("테스트유저")
                .build());

        Classes classes = classRepository.save(Classes.builder()
                .className("표정 근육 이완 클래스")
                .teacher("김강사")
                .thumbnailUrl("https://aeio-photo2.s3.ap-northeast-2.amazonaws.com/photo/thumbnail-sample.jpg")
                .time(10)
                .build());

        classLikeRepository.save(ClassLike.builder()
                .member(member)
                .classes(classes)
                .build());

        String accessToken = jwtTokenService.createAccessToken(member.getAuthId());

        // Act & Assert: 실제 HTTP 요청으로 응답 검증
        mockMvc.perform(get("/classes/likes/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("SC008"))
                .andExpect(jsonPath("$.data.likeClassList[0].classId").value(classes.getId()))
                .andExpect(jsonPath("$.data.likeClassList[0].className").value("표정 근육 이완 클래스"))
                .andExpect(jsonPath("$.data.likeClassList[0].subscribedAt").value(notNullValue()))
                // 최상위 classId(죽은 필드)와 ClassInfo의 불필요한 필드들이 더 이상 없어야 한다
                .andExpect(jsonPath("$.data.classId").doesNotExist())
                .andExpect(jsonPath("$.data.likeClassList[0].teacher").doesNotExist())
                .andExpect(jsonPath("$.data.likeClassList[0].classType").doesNotExist())
                .andExpect(jsonPath("$.data.likeClassList[0].level").doesNotExist());
    }
}
