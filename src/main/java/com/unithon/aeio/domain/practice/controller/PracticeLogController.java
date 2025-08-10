package com.unithon.aeio.domain.practice.controller;

import com.unithon.aeio.domain.practice.converter.PracticeLogConverter;
import com.unithon.aeio.domain.practice.dto.PracticeLogRequest;
import com.unithon.aeio.domain.practice.dto.PracticeLogResponse;
import com.unithon.aeio.domain.practice.service.PracticeLogService;
import com.unithon.aeio.global.result.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.unithon.aeio.global.result.code.ClassResultCode.CREATE_PRESIGNED_URL;

@RestController
@Slf4j
@RequestMapping("/practiceLog")
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
}
