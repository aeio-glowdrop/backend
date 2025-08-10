package com.unithon.aeio.domain.practice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public abstract class PracticeLogResponse {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PreSignedUrlList {
        private List<PreSignedUrl> preSignedUrlInfoList;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PreSignedUrl {
        private String preSignedUrl;
        private String photoUrl;
        private String photoName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PracticeLogId {
        private Long practiceLogId;
    }
}
