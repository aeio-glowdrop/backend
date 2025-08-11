package com.unithon.aeio.global.result.code;

import com.unithon.aeio.global.result.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClassResultCode implements ResultCode {
    CREATE_CLASS(200, "SC000", "성공적으로 클래스를 생성했습니다."),
    SUBSCRIBE_CLASS(200, "SC001", "성공적으로 클래스를 구독했습니다."),
    LIKE_CLASS(200, "SC002", "성공적으로 클래스에 좋아요를 눌렀습니다"),
    CANCEL_LIKE(200, "SC003", "성공적으로 좋아요를 취소했습니다."),
    CREATE_PRESIGNED_URL(200, "SC001", "성공적으로 presigned url을 생성했습니다."),
    CREATE_BASIC_LOG(200, "SC002", "기본 클래스의 운동 기록을 성공적으로 생성했습니다."),
    ;
    private final int status;
    private final String code;
    private final String message;
}
