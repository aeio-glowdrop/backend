package com.unithon.aeio.domain.classes.controller;

import com.unithon.aeio.domain.classes.converter.ClassConverter;
import com.unithon.aeio.domain.classes.dto.ClassRequest;
import com.unithon.aeio.domain.classes.dto.ClassResponse;
import com.unithon.aeio.domain.classes.service.ClassService;
import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.global.result.ResultResponse;
import com.unithon.aeio.global.result.code.ClassResultCode;
import com.unithon.aeio.global.security.annotation.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final ClassConverter classConverter;

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

    @GetMapping("/subsList")
    @Operation(summary = "내 구독 클래스 목록 조회 API", description = "현재 로그인 사용자가 구독 중인 클래스 목록을 반환합니다.")
    public ResultResponse<ClassResponse.SubsList> mySubs(@LoginMember Member member) {
        return ResultResponse.of(ClassResultCode.GET_MY_SUBList,
                classService.getMySubsList(member));
    }

    // 내가 좋아요한 클래스 최신순 페이징
    @GetMapping("/likes/me")
    @Operation(summary = "내가 좋아요한 클래스 목록 조회", description = "본인이 좋아요한 클래스를 최신순으로 페이징 조회합니다.")
    @Parameters({
            @Parameter(name = "page", description = "0부터 시작하는 페이지 인덱스"),
            @Parameter(name = "size", description = "페이지 크기")
    })
    public ResultResponse<ClassResponse.PagedLikeList> getMyLikedClasses(
            @LoginMember Member member,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable
    ) {
        Page<ClassResponse.ClassInfo> likeClassList = classService.getMyLikedClasses(member, pageable);
        return ResultResponse.of(ClassResultCode.LIKE_LIST,
                classConverter.toPagedLikeList(likeClassList));
    }

}