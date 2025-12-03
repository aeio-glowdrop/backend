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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

import static com.unithon.aeio.global.result.code.ClassResultCode.CREATE_BASIC_LOG;
import static com.unithon.aeio.global.result.code.ClassResultCode.CREATE_PRESIGNED_URL;
import static com.unithon.aeio.global.result.code.ClassResultCode.GET_PRACTICE_LIST;
import static com.unithon.aeio.global.result.code.ClassResultCode.GET_PRACTICE_LIST_BY_DATE;

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
    @Operation(summary = "일별 운동정보 저장 API", description = "베이직 클래스를 수행했을 때 일별 운동정보를 저장하는 API 입니다.")
    public ResultResponse<PracticeLogResponse.PracticeLogId> uploadPhotos(@RequestParam("classId") Long classId,
                                                                          @LoginMember Member member,
                                                                          @RequestBody @Valid PracticeLogRequest.BasicLog request) {
        return ResultResponse.of(CREATE_BASIC_LOG,
                practiceLogService.createPracticeLog(classId, member, request));
    }

    @GetMapping("/by-date")
    @Operation(summary = "특정 날짜의 운동 목록 조회 API", description = "특정 날짜의 운동 목록을 최신순으로 반환합니다.")
    public ResultResponse<List<PracticeLogResponse.PracticeItem>> getPracticeListByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @LoginMember Member member) {
        return ResultResponse.of(GET_PRACTICE_LIST_BY_DATE,
                practiceLogService.getPracticeListByDate(date, member)
        );
    }

    @GetMapping("/date-list")
    @Operation(summary = "특정 멤버의 운동 날짜 조회 API", description = "특정 멤버의 운동 날짜 리스트 반환")
    public ResultResponse<List<PracticeLogResponse.PracticeDate>> getPracticeDates(@LoginMember Member member) {
        return ResultResponse.of(GET_PRACTICE_LIST,
                practiceLogService.getPracticeDateList(member)
        );
    }
}
