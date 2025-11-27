package com.unithon.aeio.domain.member.dto;

import lombok.Data;

import java.util.List;

@Data
public class ApplePublicKeysResponse {
    private List<ApplePublicKeyDto> keys;
}
