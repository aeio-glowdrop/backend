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
import java.time.YearMonth;
import java.util.List;

import static com.unithon.aeio.global.result.code.ClassResultCode.CREATE_BASIC_LOG;
import static com.unithon.aeio.global.result.code.ClassResultCode.CREATE_PRESIGNED_URL;
import static com.unithon.aeio.global.result.code.ClassResultCode.GET_PRACTICE_LIST;
import static com.unithon.aeio.global.result.code.ClassResultCode.GET_PRACTICE_LIST_BY_DATE;
import static com.unithon.aeio.global.result.code.ClassResultCode.GET_PRACTICE_LIST_BY_MONTH;
import static com.unithon.aeio.global.result.code.ClassResultCode.GET_CLASS_STREAK;
import static com.unithon.aeio.global.result.code.ClassResultCode.GET_TOTAL_COUNT;

@RestController
@Slf4j
@RequestMapping("/practice")
@Tag(name = "03. 운동정보 API", description = "운동정보를 CRUD하는 API입니다.")
@RequiredArgsConstructor
public class PracticeLogController {

    private final PracticeLogService practiceLogService;
    private final PracticeLogConverter practiceLogConverter;

    @PostMapping("/preSignedUrl")
    @Operation(summary = "PresignedURL 리스트 발급 API", description = "S3 직접 업로드를 위한 PUT presigned URL 목록을 발급합니다(인증 불필요)")
    public ResultResponse<PracticeLogResponse.PreSignedUrlList> getPreSignedUrlList(@Valid @RequestBody PracticeLogRequest.PreSignedUrlRequest request) {
        long startTime = System.currentTimeMillis();
        List<PracticeLogResponse.PreSignedUrl> preSignedUrlList = practiceLogService.getPreSignedUrlList(request);
        long finishTime = System.currentTimeMillis();
        log.info("PhotoServiceImpl.getPreSignedUrlList() 수행 시간: {} ms", finishTime - startTime);
        return ResultResponse.of(CREATE_PRESIGNED_URL, practiceLogConverter.toPreSignedUrlList(preSignedUrlList));
    }

    @PostMapping("/basic")
    @Operation(summary = "운동 정보 저장 API", description = "운동 전/후 사진과 피드백을 저장하고, 구독 클래스의 운동 횟수/누적시간을 갱신합니다.")
    public ResultResponse<PracticeLogResponse.PracticeLogId> uploadPhotos(@RequestParam("classId") Long classId,
                                                                          @LoginMember Member member,
                                                                          @RequestBody @Valid PracticeLogRequest.BasicLog request) {
        return ResultResponse.of(CREATE_BASIC_LOG,
                practiceLogService.createPracticeLog(classId, member, request));
    }

    @GetMapping("/by-date")
    @Operation(summary = "특정 날짜의 내 운동 목록 조회 API", description = "특정 날짜의 운동 기록을 최신순으로 조회합니다.")
    public ResultResponse<List<PracticeLogResponse.PracticeItem>> getPracticeListByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @LoginMember Member member) {
        return ResultResponse.of(GET_PRACTICE_LIST_BY_DATE,
                practiceLogService.getPracticeListByDate(date, member)
        );
    }

    @GetMapping("/date-list")
    @Operation(summary = "내 운동 날짜 리스트 조회 API", description = "캘린더 표시용으로, 내 운동 기록이 있는 날짜 목록을 반환합니다.")
    public ResultResponse<List<PracticeLogResponse.PracticeDate>> getPracticeDates(@LoginMember Member member) {
        return ResultResponse.of(GET_PRACTICE_LIST,
                practiceLogService.getPracticeDateList(member)
        );
    }

    @GetMapping("/by-month")
    @Operation(summary = "특정 년-월의 내 운동 날짜 리스트 조회 API", description = "캘린더 표시용으로, 특정 년-월에 내 운동 기록이 있는 날짜 목록을 반환합니다.")
    public ResultResponse<List<PracticeLogResponse.PracticeDate>> getPracticeDatesByMonth(
            @RequestParam("yearMonth") @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth,
            @LoginMember Member member) {
        return ResultResponse.of(GET_PRACTICE_LIST_BY_MONTH,
                practiceLogService.getPracticeDateListByMonth(yearMonth, member)
        );
    }

    @GetMapping("/total-count")
    @Operation(summary = "클래스별 내가 운동한 총 횟수 조회 API", description = "유저가 특정 클래스에서 수행한 누적 운동 횟수를 조회합니다.")
    public ResultResponse<PracticeLogResponse.TotalCount> getTotalCount(
            @RequestParam("classId") Long classId,
            @LoginMember Member member) {
        return ResultResponse.of(GET_TOTAL_COUNT,
                practiceLogService.getTotalCount(classId, member));
    }

    @GetMapping("/streak")
    @Operation(summary = "클래스별 내 연속 운동일수 조회 API", description = "특정 클래스를 오늘 기준 며칠 연속 수행했는지 조회합니다.")
    public ResultResponse<PracticeLogResponse.ClassStreak> getClassStreak(
            @RequestParam("classId") Long classId,
            @LoginMember Member member) {
        return ResultResponse.of(GET_CLASS_STREAK,
                practiceLogService.getClassStreak(classId, member));
    }
}
