package com.unithon.aeio.domain.classes.controller;

import com.unithon.aeio.domain.member.entity.Member;
import com.unithon.aeio.domain.classes.converter.PracticeLogConverter;
import com.unithon.aeio.domain.classes.dto.PracticeLogRequest;
import com.unithon.aeio.domain.classes.dto.PracticeLogResponse;
import com.unithon.aeio.domain.classes.service.PracticeLogService;
import com.unithon.aeio.global.result.ResultResponse;
import com.unithon.aeio.global.security.annotation.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.unithon.aeio.global.result.code.ClassResultCode.CREATE_BASIC_LOG;
import static com.unithon.aeio.global.result.code.ClassResultCode.CREATE_PRESIGNED_URL;

@RestController
@Slf4j
@RequestMapping("/practice")
@Tag(name = "03. 일별 운동정보 API", description = "일별 운동정보를 CRUD하는 API입니다.")
@RequiredArgsConstructor
public class PracticeLogController {

    private final PracticeLogService practiceLogService;
    private final PracticeLogConverter practiceLogConverter;

    @PostMapping("/preSignedUrl")
    @Operation(summary = "Presigned URL 요청 API", description = "Presigned URL을 요청하는 API입니다.")
    public ResultResponse<PracticeLogResponse.PreSignedUrlList> getPreSignedUrlList(@Valid @RequestBody PracticeLogRequest.PreSignedUrlRequest request) {
        long startTime = System.currentTimeMillis();
        List<PracticeLogResponse.PreSignedUrl> preSignedUrlList = practiceLogService.getPreSignedUrlList(request);
        long finishTime = System.currentTimeMillis();
        log.info("PhotoServiceImpl.getPreSignedUrlList() 수행 시간: {} ms", finishTime - startTime);
        return ResultResponse.of(CREATE_PRESIGNED_URL, practiceLogConverter.toPreSignedUrlList(preSignedUrlList));
    }

    @PostMapping("/basic")
    @Operation(summary = "베이직 클래스 - 일별 운동정보 저장 API", description = "베이직 클래스를 수행했을 때 일별 운동정보를 저장하는 API 입니다.")
    public ResultResponse<PracticeLogResponse.PracticeLogId> uploadPhotos(@RequestParam("classId") Long classId,
                                                                          @LoginMember Member member,
                                                                          @RequestBody @Valid PracticeLogRequest.BasicLog request) {
        return ResultResponse.of(CREATE_BASIC_LOG,
                practiceLogService.createBasicLog(classId, member, request));
    }
}
