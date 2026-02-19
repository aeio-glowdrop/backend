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
    CREATE_PRESIGNED_URL(200, "SC004", "성공적으로 presigned url을 생성했습니다."),
    CREATE_BASIC_LOG(200, "SC005", "기본 클래스의 운동 기록을 성공적으로 생성했습니다."),
    GET_MY_SUBList(200, "SC006", "구독중인 클래스 목록을 성공적으로 조회했습니다."),
    GET_PRACTICE_LIST_BY_DATE(200, "SC007", "특정 날짜의 운동 리스트를 조회했습니다."),
    GET_PRACTICE_LIST(200, "SC007", "특정 멤버의 운동 날짜 리스트를 조회했습니다."),
    LIKE_LIST(200, "SC008", "사용자가 좋아요한 클래스의 목록을 페이징 조회했습니다."),
    UNSUBSCRIBE_CLASS(200, "SM009", "성공적으로 클래스 구독을 취소했습니다."),
    DELETE_CLASS(200, "SM010", "성공적으로 클래스를 삭제하였습니다."),
    GET_CLASS_INFO(200, "SM011", "성공적으로 클래스 정보를 조회했습니다."),
    GET_TOTAL_COUNT(200, "SC012", "운동 총 횟수를 성공적으로 조회했습니다."),
    GET_CLASS_STREAK(200, "SC013", "클래스별 연속 운동 일수를 성공적으로 조회했습니다."),
    ;
    private final int status;
    private final String code;
    private final String message;
}
