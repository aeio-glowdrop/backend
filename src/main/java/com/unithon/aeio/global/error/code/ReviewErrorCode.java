package com.unithon.aeio.global.error.code;

import com.unithon.aeio.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {
    RATE_REQUIRED(400, "ER000", "별점은 필수로 입력해야 합니다."),
    RATE_OUT_OF_RANGE(400, "ER001", "별점은 0.0 이상 5.0 이하로 입력해야 합니다."),
    RATE_MUST_BE_HALF_STEP(400, "ER002", "별점은 0.5 단위로만 입력할 수 있습니다."),
    REVIEW_NOT_FOUND(400, "ER003", "해당하는 리뷰가 없습니다."),
    REVIEW_AUTH_ORBIDDEN(400, "ER004", "본인이 작성한 리뷰가 아니므로 삭제할 수 없습니다."),
    ;

    private final int status;
    private final String code;
    private final String message;
}
