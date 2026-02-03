package com.unithon.aeio.domain.member.dto;

import com.unithon.aeio.domain.member.entity.Worry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public abstract class MemberResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberId {
        private Long memberId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfo {
        private long memberId;
        private String nickName;
        private String profileImage;
        private String email;
        private List<String> worryList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Streak {
        private LocalDate today;
        private int streakDays;
    }
}
