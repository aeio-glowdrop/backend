package com.unithon.aeio.domain.classes.controller;

import com.unithon.aeio.domain.classes.dto.ClassRequest;
import com.unithon.aeio.domain.classes.dto.ClassResponse;
import com.unithon.aeio.domain.classes.service.ClassService;
import com.unithon.aeio.global.result.ResultResponse;
import com.unithon.aeio.global.result.code.ClassResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
