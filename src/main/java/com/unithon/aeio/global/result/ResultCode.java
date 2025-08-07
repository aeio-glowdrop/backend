package com.unithon.aeio.global.result;

/**
 * 결과 코드에 대한 Enum class
 * 결과 코드 형식
 * 1. 모든 결과 코드는 "S"로 시작한다.
 * 2. 2번째 문자는 결과가 발생한 카테고리를 나타낸다.
 * ex) "M": Member, "SP": SalePost, "B": Book, "R": Report
 *
 */
public interface ResultCode {
    int getStatus();
    String getCode();
    String getMessage();
}
