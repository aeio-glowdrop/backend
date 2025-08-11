package com.unithon.aeio.domain.classes.controller;

import com.unithon.aeio.domain.classes.dto.ClassRequest;
import com.unithon.aeio.domain.classes.dto.ClassResponse;
import com.unithon.aeio.domain.classes.service.ClassService;
import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.global.result.ResultResponse;
import com.unithon.aeio.global.result.code.ClassResultCode;
import com.unithon.aeio.global.security.annotation.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.unithon.aeio.global.result.code.ClassResultCode.CANCEL_LIKE;

@RestController
@RequestMapping("/classes")
@Tag(name = "02. 클래스 API", description = "클래스 도메인의 API입니다.")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;

    @PostMapping
    @Operation(summary = "클래스 생성 API", description = "새로운 클래스를 생성하는 API입니다.")
    public ResultResponse<ClassResponse.ClassId> createClass(@RequestBody @Valid ClassRequest.ClassInfo request) {
        return ResultResponse.of(ClassResultCode.CREATE_CLASS,
                classService.createClass(request));
    }

    @PostMapping("/subs")
    @Operation(summary = "클래스 구독 API", description = "사용자가 클래스를 구독하는 API입니다.")
    public ResultResponse<ClassResponse.MemberClassId> subsClass(@RequestParam("classId") Long classId,
                                                             @LoginMember Member member) {
        return ResultResponse.of(ClassResultCode.SUBSCRIBE_CLASS,
                classService.subsClass(classId, member));
    }

    @PostMapping("/like")
    @Operation(summary = "클래스 좋아요 API", description = "사용자가 클래스를 좋아요하는 API입니다.")
    public ResultResponse<ClassResponse.LikeInfo> likeClass(@RequestParam("classId") Long classId,
                                                            @LoginMember Member member) {
        return ResultResponse.of(ClassResultCode.LIKE_CLASS,
                classService.likeClass(classId, member));
    }

    @DeleteMapping("/cancelLike")
    @Operation(summary = "클래스 좋아요 취소 API",
            description = "로그인한 사용자가 특정 클래스에 대해 좋아요를 취소합니다. (hard delete)")
    public ResultResponse<ClassResponse.ClassId> cancelLike(@RequestParam("classId") Long classId,
                                                            @LoginMember Member member) {
        return ResultResponse.of(CANCEL_LIKE,
                classService.cancelLike(classId, member));
    }


}
