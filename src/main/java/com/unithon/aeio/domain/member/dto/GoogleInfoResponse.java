package com.unithon.aeio.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleInfoResponse {
    private String sub;
    private String name;
    private String email;
    private String picture;

    public String getAuthId() {
        return sub;
    }
}
