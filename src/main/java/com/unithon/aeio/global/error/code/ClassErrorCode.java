package com.unithon.aeio.global.error.code;

import com.unithon.aeio.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClassErrorCode implements ErrorCode {
    CLASS_NOT_FOUND(405, "EC000", "해당 클래스를 찾을 수 없습니다"),
    ALREADY_SUBSCRIBED(405, "EG001", "이미 구독한 클래스입니다."),
    ;

    private final int status;
    private final String code;
    private final String message;
}
