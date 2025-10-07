package com.unithon.aeio.global.result.code;

import com.unithon.aeio.global.result.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewResultCode implements ResultCode {
    CREATE_REVIEW(200, "SR000", "성공적으로 리뷰를 생성했습니다."),
    DELETE_REVIEW(200, "SR001", "성공적으로 리뷰를 삭제했습니다."),
    REVIEW_LIST(200, "SR002", "성공적으로 리뷰 리스트를 조회했습니다."),
    ;
    private final int status;
    private final String code;
    private final String message;
}
