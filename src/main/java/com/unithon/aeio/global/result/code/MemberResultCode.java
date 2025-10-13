package com.unithon.aeio.global.result.code;

import com.unithon.aeio.global.result.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberResultCode implements ResultCode {
    LOGIN(200, "SM000", "성공적으로 로그인하였습니다."),
    REFRESH_TOKEN(200, "SM001", "성공적으로 리프레쉬 토큰을 발급했습니다."),
    CHECK_MEMBER_REGISTRATION(200, "SM002", "해당 정보에 대응하는 회원의 가입 여부를 성공적으로 조회하였습니다."),
    CREATE_MEMBER(200, "SM003", "성공적으로 멤버 정보를 저장했습니다."),
    GET_NICKNAME(200, "SM004", "성공적으로 닉네임을 조회했습니다."),
    GET_CURRENT_STREAK(200, "SM005", "성공적으로 현재 스트릭 수를 조회했습니다."),
    UPDATE_MEMBER(200, "SM006", "성공적으로 멤버 정보를 수정했습니다."),
    ;
    private final int status;
    private final String code;
    private final String message;
}
