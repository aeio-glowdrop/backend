package com.unithon.aeio.global.result.code;

import com.unithon.aeio.global.result.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClassResultCode implements ResultCode {
    CREATE_CLASS(200, "SC000", "성공적으로 클래스를 생성했습니다."),
    CREATE_PRESIGNED_URL(200, "SC001", "성공적으로 presigned url을 생성했습니다."),
    CREATE_BASIC_LOG(200, "SC002", "기본 클래스의 운동 기록을 성공적으로 생성했습니다."),
    ;
    private final int status;
    private final String code;
    private final String message;
}
