package com.unithon.aeio.domain.member.dto;

import com.unithon.aeio.domain.member.entity.Gender;
import com.unithon.aeio.domain.member.entity.Worry;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public abstract class MemberRequest {

    // 멤버 정보입력
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfo {

        @NotBlank(message = "닉네임은 필수로 입력해야 합니다.")
        @Size(max = 9, message = "닉네임은 최대 9자까지 설정할 수 있습니다.")
        private String nickName;

        @NotNull(message = "성별은 필수로 입력해야 합니다.")
        private Gender gender;

        @NotNull(message = "고민 부위는 최소 1개 이상이어야 합니다.")
        @Size(max = 8, message = "고민 부위는 최대 8개까지 선택할 수 있습니다.")
        private List<@NotBlank @Size(max = 10, message = "고민부위 이름은 최대 10자입니다.") String> worryList;
    }

}