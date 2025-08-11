package com.unithon.aeio.global.error.code;

import com.unithon.aeio.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClassErrorCode implements ErrorCode {
    CLASS_NOT_FOUND(405, "EC000", "해당 클래스를 찾을 수 없습니다"),
    ALREADY_SUBSCRIBED(405, "EG001", "이미 구독한 클래스입니다."),
    ALREADY_LIKED(405, "EG002", "이미 좋아요를 누른 클래스입니다."),
    NOT_LIKED(404, "EP003", "좋아요가 눌려 있지 않은 사진이기 때문에, 좋아요를 취소할 수 없습니다."),
    MEMBER_CLASS_NOT_FOUND(404, "EP004", "해당 구독 정보가 존재하지 않습니다."),
    ;

    private final int status;
    private final String code;
    private final String message;
}
