package com.unithon.aeio.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class AppleInfoResponse {
    private String authId;  // 애플 sub
    private String email;   // 있을 수도 있고, 없을 수도 있음
}