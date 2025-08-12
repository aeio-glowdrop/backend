package com.unithon.aeio.domain.classes.dto;

import com.unithon.aeio.domain.classes.entity.ClassType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public abstract class ClassRequest {

    // 클래스 정보입력
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassInfo {

        @NotNull(message = "수업 유형(classType)은 필수입니다.")
        private ClassType classType;
        @NotBlank(message = "선생님 입력은 필수입니다.")
        @Size(max = 9, message = "이름은 최대 9자까지 입력할 수 있습니다.")
        private String teacher;
        @NotBlank(message = "클래스 이름 입력은 필수입니다.")
        @Size(max = 30, message = "클래스 이름은 최대 30자까지 입력할 수 있습니다.")
        private String className;
        private String thumbnailUrl;
    }
}
