package com.unithon.aeio.global.error;

public interface ErrorCode {
    int getStatus();
    String getCode();
    String getMessage();
}
