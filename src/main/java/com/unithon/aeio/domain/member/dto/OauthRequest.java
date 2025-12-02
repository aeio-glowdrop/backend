package com.unithon.aeio.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public abstract class OauthRequest {

    @Getter
    public static class FrontAccessTokenInfo {
        @NotNull(message = "프론트 액세스토큰을 필수로 입력해야 합니다.")
        private String accessToken;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        @NotNull(message = "카카오에서 제공한 회원 번호를 필수로 입력해야 합니다.")
        private String authId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AgreementRequest {

        @AssertTrue(message = "이용약관에 동의해야 합니다.")
        private Boolean termsAgree;

        @AssertTrue(message = "개인정보처리방침에 동의해야 합니다.")
        private Boolean privacyAgree;

        @AssertTrue(message = "개인정보 수집·이용에 동의해야 합니다.")
        private Boolean personalInfoAgree;

        @AssertTrue(message = "만 14세 이상이어야 합니다.")
        private Boolean ageOver14;

        @Schema(description = "마케팅 정보 수신 동의 여부 (선택)")
        private Boolean marketingAgree;

        @NotBlank(message = "이용약관 버전은 필수입니다.")
        private String termsVersion;

        @NotBlank(message = "개인정보처리방침 버전은 필수입니다.")
        private String privacyVersion;
    }
}