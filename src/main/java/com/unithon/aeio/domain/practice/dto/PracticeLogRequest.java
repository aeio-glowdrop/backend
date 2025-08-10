package com.unithon.aeio.domain.practice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public abstract class PracticeLogRequest {

    // presigned url 요청
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PreSignedUrlRequest {
        @NotEmpty(message = "사진의 이름은 하나 이상이어야 합니다.")
        private List<String> photoNameList;
    }

    // 베이직 클래스 운동기록 생성
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasicLog {
        @NotEmpty(message = "무표정 사진url은 필수로 입력해야 합니다.")
        private String expressionlessPhoto;
        @Size(max=100, message = "최대 100자까지 입력할 수 있습니다.")
        private String feedBack;
        private Integer count;
    }


}
