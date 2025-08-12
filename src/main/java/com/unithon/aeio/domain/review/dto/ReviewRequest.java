package com.unithon.aeio.domain.review.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public abstract class ReviewRequest {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewInfo {
        @NotNull(message = "별점은 필수입니다.")
        @DecimalMin(value = "0.0", message = "별점은 0.0 이상이어야 합니다.")
        @DecimalMax(value = "5.0", message = "별점은 5.0 이하여야 합니다.")
        @Digits(integer = 1, fraction = 1, message = "별점은 소수 첫째 자리까지 입력해야 합니다. (예: 4.5)")
        private Double rate;

        @Size(max = 300, message = "리뷰는 최대 300자까지 입력 가능합니다.")
        private String reviewText;

        @NotNull
        @Size(max = 10, message = "리뷰 사진은 최대 10개까지 업로드 가능합니다.")
        private List<@NotBlank String> photoUrl;
    }
}
